/**
 * Copyright (C) 2009 STMicroelectronics
 *
 * This file is part of "Mind Compiler" is free software: you can redistribute 
 * it and/or modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: mind@ow2.org
 *
 * Authors: Matthieu Leclercq
 * Contributors: 
 */

package org.ow2.mind.adl.generic;

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.ow2.mind.adl.ADLErrors;
import org.ow2.mind.adl.DefinitionReferenceResolver.AbstractDelegatingDefinitionReferenceResolver;
import org.ow2.mind.adl.ast.DefinitionReference;
import org.ow2.mind.adl.generic.ast.TypeArgument;
import org.ow2.mind.adl.generic.ast.TypeArgumentContainer;
import org.ow2.mind.error.ErrorManager;

import com.google.inject.Inject;

public class NoAnyTypeArgumentDefinitionReferenceResolver
    extends
      AbstractDelegatingDefinitionReferenceResolver {

  @Inject
  protected ErrorManager errorManagerItf;

  // ---------------------------------------------------------------------------
  // Implementation of the DefinitionReferenceResolver interface
  // ---------------------------------------------------------------------------

  public Definition resolve(final DefinitionReference reference,
      final Definition encapsulatingDefinition,
      final Map<Object, Object> context) throws ADLException {
    checkReference(reference);
    return clientResolverItf.resolve(reference, encapsulatingDefinition,
        context);
  }

  protected void checkReference(final DefinitionReference reference)
      throws ADLException {
    if (reference instanceof TypeArgumentContainer) {
      for (final TypeArgument typeArgument : ((TypeArgumentContainer) reference)
          .getTypeArguments()) {
        if (typeArgument.getDefinitionReference() == null
            && typeArgument.getTypeParameterReference() == null) {
          errorManagerItf.logError(
              ADLErrors.INVALID_REFERENCE_ANY_TEMPLATE_VALUE, typeArgument);
        }
      }
    }
  }
}
