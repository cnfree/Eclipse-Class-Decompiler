
package org.sf.feeling.decompiler.procyon.decompiler;

import org.eclipse.jface.action.Action;
import org.sf.feeling.decompiler.editor.BaseDecompilerSourceMapper;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.editor.IDecompilerDescriptor;
import org.sf.feeling.decompiler.procyon.ProcyonDecompilerPlugin;
import org.sf.feeling.decompiler.procyon.actions.DecompileWithProcyonAction;

public class ProcyonDecompilerDescriptor implements IDecompilerDescriptor
{

	private ProcyonDecompiler decompiler = null;

	private BaseDecompilerSourceMapper sourceMapper = null;

	private Action decompileAction = null;

	public String getDecompilerType( )
	{
		return ProcyonDecompilerPlugin.decompilerType;
	}

	public String getDecompilerPreferenceLabel( )
	{
		return "Procyon ( Slow, Support JDK8 )";
	}

	public IDecompiler getDecompiler( )
	{
		if ( decompiler == null )
			decompiler = new ProcyonDecompiler( );
		return decompiler;
	}

	public BaseDecompilerSourceMapper getDecompilerSourceMapper( )
	{
		if ( sourceMapper == null )
		{
			sourceMapper = new ProcyonSourceMapper( );
		}
		return sourceMapper;
	}

	public Action getDecompileAction( )
	{
		if ( decompileAction == null )
		{
			decompileAction = new DecompileWithProcyonAction( );
		}
		return decompileAction;
	}

	public boolean isEnabled( )
	{
		return !( System.getProperty( "java.version" ).compareTo( "1.7" ) < 0 );
	}

}
