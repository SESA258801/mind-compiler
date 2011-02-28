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

package org.ow2.mind.adl;

import static org.testng.Assert.assertNotNull;

import java.util.HashMap;

import org.objectweb.fractal.adl.Definition;
import org.objectweb.fractal.adl.Loader;
import org.ow2.mind.CommonFrontendModule;
import org.ow2.mind.adl.imports.ImportDefinitionReferenceResolver;
import org.ow2.mind.adl.parser.ADLParser;
import org.ow2.mind.plugin.PluginLoaderModule;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestDefinitionReferenceResolver {

  Loader                  loader;

  HashMap<Object, Object> context;

  ASTChecker              checker;

  @BeforeMethod(alwaysRun = true)
  protected void setUp() throws Exception {
    final Injector injector = Guice.createInjector(new CommonFrontendModule(),
        new PluginLoaderModule(), new AbstractADLFrontendModule() {

          protected void configureTest() {
            bind(Loader.class).toChainStartingWith(ErrorLoader.class)
                .followedBy(CacheLoader.class).followedBy(ExtendsLoader.class)
                .followedBy(SubComponentResolverLoader.class)
                .endingWith(ADLParser.class);

            bind(DefinitionReferenceResolver.class)
                .toChainStartingWith(CachingDefinitionReferenceResolver.class)
                .followedBy(ImportDefinitionReferenceResolver.class)
                .endingWith(BasicDefinitionReferenceResolver.class);

            setDefaultExtendsLoaderConfig();
            setDefaultSubComponentLoaderConfig();
          }
        });

    loader = injector.getInstance(Loader.class);

    context = new HashMap<Object, Object>();

    checker = new ASTChecker();
  }

  @Test(groups = {"functional"})
  public void test1() throws Exception {
    final Definition def = loader.load("pkg1.pkg2.Primitive1", context);
    assertNotNull(def);
  }

  @Test(groups = {"functional"})
  public void test2() throws Exception {
    final Definition def = loader.load("pkg1.Composite1", context);
    checker.assertDefinition(def).containsComponent("subComp1")
        .isAnInstanceOf("pkg1.pkg2.Primitive1");
  }

  @Test(groups = {"functional"})
  public void test3() throws Exception {
    final Definition def = loader.load("pkg1.Composite2", context);
    checker.assertDefinition(def).containsComponent("subComp1")
        .isAnInstanceOf("pkg1.Composite1");
  }
}
