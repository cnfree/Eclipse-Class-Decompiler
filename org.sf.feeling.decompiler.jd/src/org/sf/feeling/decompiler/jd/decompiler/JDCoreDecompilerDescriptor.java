
package org.sf.feeling.decompiler.jd.decompiler;

import jd.ide.eclipse.editors.JDSourceMapper;

import org.eclipse.jface.action.Action;
import org.sf.feeling.decompiler.editor.BaseDecompilerSourceMapper;
import org.sf.feeling.decompiler.editor.IDecompiler;
import org.sf.feeling.decompiler.editor.IDecompilerDescriptor;
import org.sf.feeling.decompiler.jd.JDCoreDecompilerPlugin;
import org.sf.feeling.decompiler.jd.actions.DecompileWithJDCoreAction;

public class JDCoreDecompilerDescriptor implements IDecompilerDescriptor
{

	private JDCoreDecompiler decompiler = null;

	private BaseDecompilerSourceMapper sourceMapper = null;

	private Action decompileAction = null;

	public String getDecompilerType( )
	{
		return JDCoreDecompilerPlugin.decompilerType;
	}

	public String getDecompilerPreferenceLabel( )
	{
		return "JD-Core ( Fast, Recommend  )";
	}

	public IDecompiler getDecompiler( )
	{
		if ( decompiler == null )
			decompiler = new JDCoreDecompiler( (JDSourceMapper) getDecompilerSourceMapper( ) );
		return decompiler;
	}

	public BaseDecompilerSourceMapper getDecompilerSourceMapper( )
	{
		if ( sourceMapper == null )
		{
			sourceMapper = new JDCoreSourceMapper( );
		}
		return sourceMapper;
	}

	public Action getDecompileAction( )
	{
		if ( decompileAction == null )
		{
			decompileAction = new DecompileWithJDCoreAction( );
		}
		return decompileAction;
	}

	public boolean isEnabled( )
	{
		return true;
	}

}
