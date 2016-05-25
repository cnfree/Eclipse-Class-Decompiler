
package org.sf.feeling.decompiler.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

public interface IDecompilerDescriptor
{

	String getDecompilerType( );

	String getDecompilerPreferenceLabel( );

	IDecompiler getDecompiler( );

	BaseDecompilerSourceMapper getDecompilerSourceMapper( );

	Action getDecompileAction( );

	boolean isEnabled( );

	boolean isDefault( );

	ImageDescriptor getDecompilerIcon( );
}
