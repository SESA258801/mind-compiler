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

package org.ow2.mind.st;

import org.antlr.stringtemplate.StringTemplateGroupLoader;
import org.objectweb.fractal.adl.xml.XMLNodeFactory;
import org.objectweb.fractal.adl.xml.XMLNodeFactoryImpl;
import org.ow2.mind.st.templates.parser.StringTemplateLoader;

public final class STLoaderFactory {
  private STLoaderFactory() {
  }

  public static StringTemplateGroupLoader newSTLoader() {
    final StringTemplateComponentLoader stcLoader = new StringTemplateComponentLoader();
    final StringTemplateLoader templateLoader = new StringTemplateLoader();
    final XMLNodeFactory xmlNodeFactory = new XMLNodeFactoryImpl();
    // set my class loader as classloader used by XMLNodeFactory
    xmlNodeFactory.setClassLoader(STLoaderFactory.class.getClassLoader());
    templateLoader.nodeFactoryItf = xmlNodeFactory;
    stcLoader.loaderItf = templateLoader;

    return stcLoader;
  }

}
