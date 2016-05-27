
package org.sf.feeling.decompiler.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jdt.internal.core.SourceMapper;
import org.eclipse.jface.preference.IPreferenceStore;
import org.sf.feeling.decompiler.JavaDecompilerPlugin;
import org.sf.feeling.decompiler.fernflower.FernFlowerDecompiler;
import org.sf.feeling.decompiler.util.ClassUtil;
import org.sf.feeling.decompiler.util.DecompilerOutputUtil;
import org.sf.feeling.decompiler.util.SortMemberUtil;
import org.sf.feeling.decompiler.util.UIUtil;

public abstract class BaseDecompilerSourceMapper extends DecompilerSourceMapper
{

	protected IDecompiler origionalDecompiler;
	private IDecompiler usedDecompiler;
	private String classLocation;

	public BaseDecompilerSourceMapper( IPath sourcePath, String rootPath,
			Map options )
	{
		super( sourcePath, rootPath, options );
	}

	public char[] findSource( IType type, IBinaryType info )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );
		boolean always = prefs.getBoolean( JavaDecompilerPlugin.IGNORE_EXISTING );

		Collection exceptions = new LinkedList( );
		IPackageFragment pkgFrag = type.getPackageFragment( );
		IPackageFragmentRoot root = (IPackageFragmentRoot) pkgFrag.getParent( );

		char[] attachedSource = null;
		if ( originalSourceMapper.containsKey( root ) )
		{
			attachedSource = ( (SourceMapper) originalSourceMapper.get( root ) ).findSource( type,
					info );

			if ( attachedSource != null && !always )
			{
				isAttachedSource = true;
				return attachedSource;
			}
		}

		if ( info == null )
		{
			if ( always )
				return null;
			return attachedSource;
		}

		try
		{
			if ( root instanceof PackageFragmentRoot )
			{
				PackageFragmentRoot pfr = (PackageFragmentRoot) root;

				SourceMapper sourceMapper = pfr.getSourceMapper( );
				if ( sourceMapper != null
						&& !always
						&& !( sourceMapper instanceof DecompilerSourceMapper ) )
				{
					attachedSource = sourceMapper.findSource( type, info );
					if ( attachedSource != null )
					{
						isAttachedSource = true;
						return attachedSource;
					}
				}

				if ( !originalSourceMapper.containsKey( root ) )
				{
					originalSourceMapper.put( root, sourceMapper );
				}

				if ( sourceMapper != this )
				{
					pfr.setSourceMapper( this );
				}
			}
		}
		catch ( JavaModelException e )
		{
			JavaDecompilerPlugin.logError( e, "Could not set source mapper." ); //$NON-NLS-1$
		}

		isAttachedSource = false;

		String className = new String( info.getName( ) );
		String fullName = new String( info.getFileName( ) );
		className = fullName.substring( fullName.lastIndexOf( className ) );

		int index = className.lastIndexOf( '/' );
		className = className.substring( index + 1 );

		classLocation = ""; //$NON-NLS-1$

		usedDecompiler = decompile( null, type, exceptions, root, className );

		if ( usedDecompiler.getSource( ) == null
				|| usedDecompiler.getSource( ).length( ) == 0 )
		{
			if ( !DecompilerType.FernFlower.equals( usedDecompiler.getDecompilerType( ) ) )
			{
				usedDecompiler = decompile( new FernFlowerDecompiler( ),
						type,
						exceptions,
						root,
						className );
				if ( usedDecompiler.getSource( ) == null
						|| usedDecompiler.getSource( ).length( ) == 0 )
				{
					return null;
				}
			}
		}

		String code = JavaDecompilerClassFileEditor.MARK + "\r\n" //$NON-NLS-1$
				+ usedDecompiler.getSource( );

		boolean showReport = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
		if ( !showReport )
		{
			code = usedDecompiler.removeComment( code );
		}

		boolean showLineNumber = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			if ( showReport )
				code = usedDecompiler.removeComment( code );
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil( usedDecompiler.getDecompilerType( ),
					code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			boolean useSorter = prefs.getBoolean( JavaDecompilerPlugin.USE_ECLIPSE_SORTER );
			if ( useSorter )
			{
				className = new String( info.getName( ) );
				fullName = new String( info.getFileName( ) );
				if ( fullName.lastIndexOf( className ) != -1 )
				{
					className = fullName.substring( fullName.lastIndexOf( className ) );
				}

				code = SortMemberUtil.sortMember( type.getPackageFragment( )
						.getElementName( ), className, code );
			}

			source.append( formatSource( code ) );

			if ( showReport )
			{
				printDecompileReport( source,
						classLocation,
						exceptions,
						usedDecompiler.getDecompilationTime( ) );
			}
		}
		else
		{
			source.append( code );
		}

		if ( originalSourceMapper.containsKey( root ) )
		{
			( (SourceMapper) originalSourceMapper.get( root ) ).mapSource( type,
					source.toString( ).toCharArray( ),
					null );
		}
		return source.toString( ).toCharArray( );
	}

	private IDecompiler decompile( IDecompiler decompiler, IType type,
			Collection exceptions, IPackageFragmentRoot root, String className )
	{
		IDecompiler result = decompiler;

		String pkg = type.getPackageFragment( )
				.getElementName( )
				.replace( '.', '/' );

		Boolean displayNumber = null;
		if ( UIUtil.isDebugPerspective( ) )
		{
			displayNumber = JavaDecompilerPlugin.getDefault( )
					.isDisplayLineNumber( );
			JavaDecompilerPlugin.getDefault( ).displayLineNumber( Boolean.TRUE );
		}

		try
		{
			if ( root.isArchive( ) )
			{
				String archivePath = getArchivePath( root );
				classLocation += archivePath;

				if ( result == null )
				{
					try
					{
						result = ClassUtil.checkAvailableDecompiler( origionalDecompiler,
								new ByteArrayInputStream( type.getClassFile( )
										.getBytes( ) ) );
					}
					catch ( JavaModelException e )
					{
						result = origionalDecompiler;
					}
				}
				result.decompileFromArchive( archivePath, pkg, className );
			}
			else
			{
				try
				{
					classLocation += root.getUnderlyingResource( )
							.getLocation( )
							.toOSString( )
							+ "/" //$NON-NLS-1$
							+ pkg
							+ "/" //$NON-NLS-1$
							+ className;

					if ( result == null )
					{
						result = ClassUtil.checkAvailableDecompiler( origionalDecompiler,
								new File( classLocation ) );
					}
					result.decompile( root.getUnderlyingResource( )
							.getLocation( )
							.toOSString( ), pkg, className );
				}
				catch ( JavaModelException e )
				{
					exceptions.add( e );
				}
			}
		}
		catch ( Exception e )
		{
			exceptions.add( e );
		}

		if ( displayNumber != null )
		{
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( displayNumber );
		}
		return result;
	}

	public String decompile( String decompilerType, File file )
	{
		IPreferenceStore prefs = JavaDecompilerPlugin.getDefault( )
				.getPreferenceStore( );

		Boolean displayNumber = null;
		if ( UIUtil.isDebugPerspective( ) )
		{
			displayNumber = JavaDecompilerPlugin.getDefault( )
					.isDisplayLineNumber( );
			JavaDecompilerPlugin.getDefault( ).displayLineNumber( Boolean.TRUE );
		}

		IDecompiler currentDecompiler = ClassUtil.checkAvailableDecompiler( origionalDecompiler,
				file );

		currentDecompiler.decompile( file.getParentFile( ).getAbsolutePath( ),
				"", //$NON-NLS-1$
				file.getName( ) );

		if ( displayNumber != null )
		{
			JavaDecompilerPlugin.getDefault( )
					.displayLineNumber( displayNumber );
		}

		if ( currentDecompiler.getSource( ) == null
				|| currentDecompiler.getSource( ).length( ) == 0 )
			return null;

		String code = JavaDecompilerClassFileEditor.MARK + "\r\n" //$NON-NLS-1$
				+ currentDecompiler.getSource( );

		boolean showReport = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_METADATA );
		if ( !showReport )
		{
			code = currentDecompiler.removeComment( code );
		}

		boolean showLineNumber = prefs.getBoolean( JavaDecompilerPlugin.PREF_DISPLAY_LINE_NUMBERS );
		boolean align = prefs.getBoolean( JavaDecompilerPlugin.ALIGN );
		if ( ( showLineNumber && align ) || UIUtil.isDebugPerspective( ) )
		{
			if ( showReport )
				code = currentDecompiler.removeComment( code );
			DecompilerOutputUtil decompilerOutputUtil = new DecompilerOutputUtil( currentDecompiler.getDecompilerType( ),
					code );
			code = decompilerOutputUtil.realign( );
		}

		StringBuffer source = new StringBuffer( );

		if ( !UIUtil.isDebugPerspective( ) )
		{
			source.append( formatSource( code ) );

			if ( showReport )
			{
				Collection exceptions = new LinkedList( );
				exceptions.addAll( currentDecompiler.getExceptions( ) );
				printDecompileReport( source,
						file.getAbsolutePath( ),
						exceptions,
						currentDecompiler.getDecompilationTime( ) );
			}
		}
		else
		{
			source.append( code );
		}

		return source.toString( );
	}

	protected abstract void printDecompileReport( StringBuffer source,
			String location, Collection exceptions, long decompilationTime );
}
