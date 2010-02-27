/*
 * This file is part of ActivityInfo.
 *
 * ActivityInfo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ActivityInfo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ActivityInfo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010 Alex Bertram and contributors.
 */

package com.bedatadriven.rebar.g2d.it.chart.client;

import com.bedatadriven.rebar.g2d.CanvasGraphics2D;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.junit.client.GWTTestCase;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.awt.geom.Rectangle2D;

public class ChartTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "com.bedatadriven.rebar.g2d.it.chart.ChartIT";
  }

  public void testCompile() {

    DefaultPieDataset dataset = new DefaultPieDataset();
    dataset.setValue("Success", 0.75);
    dataset.setValue("Failure", 0.25);

    JFreeChart chart = ChartFactory.createPieChart("One small step for Java...", dataset, false, false, false);

    CanvasGraphics2D g2d = new CanvasGraphics2D();
    chart.draw(g2d, new Rectangle2D.Float(0, 0, 300, 400));
  }
}
