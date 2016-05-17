
package org.sf.feeling.decompiler.cfr.decompiler;

import org.eclipse.jface.action.Action;
import org.sf.feeling.decompiler.cfr.CfrDecompilerPlugin;
import org.sf.feeling.decompiler.cfr.actions.DecompileWithCfrAction;
import org.sf.feeling.decompiler.cfr.i18n.Messages;
import org.sf.feeling.decompiler.editor.BaseDecompilerSourceMapper;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.editor.IDecompilerDescriptor;

public class CfrDecompilerDescriptor implements IDecompilerDescriptor
{

	private CfrDecompiler decompiler = null;

	private BaseDecompilerSourceMapper sourceMapper = null;

	private Action decompileAction = null;

	public String getDecompilerType( )
	{
		return CfrDecompilerPlugin.decompilerType;
	}

	public String getDecompilerPreferenceLabel( )
	{
		return Messages.getString( "CfrDecompilerDescriptor.PreferenceLabel" ); //$NON-NLS-1$
	}

	public IDecompiler getDecompiler( )
	{
		if ( decompiler == null )
			decompiler = new CfrDecompiler( );
		return decompiler;
	}

	public BaseDecompilerSourceMapper getDecompilerSourceMapper( )
	{
		if ( sourceMapper == null )
		{
			sourceMapper = new CfrSourceMapper( );
		}
		return sourceMapper;
	}

	public Action getDecompileAction( )
	{
		if ( decompileAction == null )
		{
			decompileAction = new DecompileWithCfrAction( );
		}
		return decompileAction;
	}

	public boolean isEnabled( )
	{
		return !( System.getProperty( "java.version" ).compareTo( "1.6" ) < 0 ); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean isDefault( )
	{
		return false;
	}

}
