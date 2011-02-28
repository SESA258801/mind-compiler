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

package org.ow2.mind.adl.graph;

import static org.ow2.mind.adl.ast.ASTHelper.getResolvedComponentDefinition;
import static org.ow2.mind.adl.ast.ASTHelper.isType;
import static org.ow2.mind.annotation.AnnotationHelper.getAnnotation;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
import org.ow2.mind.adl.ADLErrors;
import org.ow2.mind.adl.annotation.predefined.Singleton;
import org.ow2.mind.adl.ast.Component;
import org.ow2.mind.adl.ast.ComponentContainer;
import org.ow2.mind.error.ErrorManager;

import com.google.inject.Inject;

public class BasicInstantiator implements Instantiator {

  @Inject
  protected ErrorManager errorManagerItf;

  @Inject
  protected Loader       loaderItf;

  // ---------------------------------------------------------------------------
  // Implementation of the Loader interface
  // ---------------------------------------------------------------------------

  public ComponentGraph instantiate(final Definition definition,
      final Map<Object, Object> context) throws ADLException {

    if (isType(definition))
      errorManagerItf.logError(ADLErrors.INSTANTIATE_TYPE_DEFINIITON,
          definition, definition.getName());

    final ComponentGraph graph = new ComponentGraph(definition, definition);
    if (definition instanceof ComponentContainer)
      instantiateSubComponents((ComponentContainer) definition, graph,
          new HashMap<String, ComponentGraph>(), context);
    return graph;
  }

  protected void instantiateSubComponents(final ComponentContainer container,
      final ComponentGraph graph, final Map<String, ComponentGraph> singletons,
      final Map<Object, Object> context) throws ADLException {
    for (final Component subComponent : container.getComponents()) {
      final Definition subCompDef = getResolvedComponentDefinition(
          subComponent, loaderItf, context);
      assert subCompDef != null && !isType(subCompDef);

      if (isSingleton(subCompDef)) {
        ComponentGraph singleton = singletons.get(subCompDef.getName());
        if (singleton == null) {
          singleton = new ComponentGraph(subCompDef, subComponent);
          singletons.put(subCompDef.getName(), singleton);
          if (subCompDef instanceof ComponentContainer)
            instantiateSubComponents((ComponentContainer) subCompDef,
                singleton, singletons, context);
        }
        graph.addSubComponent(singleton, subComponent.getName());

      } else {
        final ComponentGraph subGraph = new ComponentGraph(subCompDef,
            subComponent);
        if (subCompDef instanceof ComponentContainer)
          instantiateSubComponents((ComponentContainer) subCompDef, subGraph,
              singletons, context);
        graph.addSubComponent(subGraph, subComponent.getName());
      }
    }
  }

  protected boolean isSingleton(final Definition subCompDef) {
    return getAnnotation(subCompDef, Singleton.class) != null;
  }
}
