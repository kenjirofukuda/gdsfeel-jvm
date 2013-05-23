/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.gdsfeel.elements;

import java.awt.geom.Point2D;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author cs
 */
public class GdsPath extends GdsPrimitiveElement {
  private static Log log = LogFactory.getLog(GdsPath.class);

  private int _pathtype;
  private double _width;

  
  public GdsPath() {
    super();
    _pathtype = 0;
    _width = 0.0;
  }


  public double getWidth() {
    return _width;
  }


  @Override
  public Point2D[] outlinePoints() {
    Point2D[] result = pathOutlinePoints();
    if (result.length == 0) {
      return super.outlinePoints();
    }
    return result;
  }

  
  @Override
  public void setAttributes(Map<String,Object> attrs) {
    super.setAttributes(attrs);
    if (attrs.containsKey("pathtype")) {
      _pathtype = (Integer) attrs.get("pathtype");
    }
    if (attrs.containsKey("width")) {
      _width = (Double) attrs.get("width");
    }
  }


  Point2D[] pathOutlinePoints() {
    if (getWidth() == 0.0) {
      return new Point2D.Double[0];
    }
    double hw = getWidth() / 2.0;
    int numpoints = getVertices().length;
    if (numpoints < 2) {
      log.error(
            "PathToBoundary(): don't know to handle wires < 2 pts yet");
      return new Point2D.Double[0];
    }
    Point2D[] points = new Point2D.Double[numpoints * 2 + 1];
    for (int i = 0 ; i < points.length; i++) {
      points[i] = new Point2D.Double();
    }

    Point2D deltaxy = getEndDeltaXY(hw, getVertices()[0], getVertices()[1]);

    if (_pathtype == 0) {
      points[0].setLocation(
          getVertices()[0].getX() + deltaxy.getX(),
          getVertices()[0].getY() + deltaxy.getY());
      points[2 * numpoints].setLocation(
          getVertices()[0].getX() + deltaxy.getX(),
          getVertices()[0].getY() + deltaxy.getY());
      points[2 * numpoints - 1].setLocation(
          getVertices()[0].getX() - deltaxy.getX(),
          getVertices()[0].getY() - deltaxy.getY());
    }
    else {
      points[0].setLocation(
          getVertices()[0].getX() + deltaxy.getX() - deltaxy.getY(),
          getVertices()[0].getY() + deltaxy.getY() - deltaxy.getX());
      points[2 * numpoints].setLocation(
          getVertices()[0].getX() + deltaxy.getX() - deltaxy.getY(),
          getVertices()[0].getY() + deltaxy.getY() - deltaxy.getX());
      points[2 * numpoints - 1].setLocation(
          getVertices()[0].getX() - deltaxy.getX() - deltaxy.getY(),
          getVertices()[0].getY() - deltaxy.getY() - deltaxy.getX());
    }
 
    for (int i = 1; i < numpoints - 1; i++) {
      deltaxy = getDeltaXY(hw,
                          getVertices()[i - 1],
                          getVertices()[i],
                          getVertices()[i + 1]);
      points[i].setLocation(
          getVertices()[i].getX() + deltaxy.getX(),
          getVertices()[i].getY() + deltaxy.getY());
      points[2 * numpoints - i - 1].setLocation(
          getVertices()[i].getX() - deltaxy.getX(),
          getVertices()[i].getY() - deltaxy.getY());
    }

    deltaxy = getEndDeltaXY(hw, getVertices()[numpoints - 2],
                        getVertices()[numpoints - 1]);
    if (_pathtype == 0) {
      points[numpoints - 1].setLocation(
          getVertices()[numpoints - 1].getX() + deltaxy.getX(),
          getVertices()[numpoints - 1].getY() + deltaxy.getY());
      points[numpoints].setLocation(
          getVertices()[numpoints - 1].getX() - deltaxy.getX(),
          getVertices()[numpoints - 1].getY() - deltaxy.getY());
    }
    else {
      points[numpoints - 1].setLocation(
          getVertices()[numpoints - 1].getX() + deltaxy.getX() + deltaxy.getY(),
          getVertices()[numpoints - 1].getY() + deltaxy.getY() + deltaxy.getX());
      points[numpoints].setLocation(
          getVertices()[numpoints - 1].getX() - deltaxy.getX() + deltaxy.getY(),
          getVertices()[numpoints - 1].getY() - deltaxy.getY() + deltaxy.getX());
    }
    return points;
  }
  

  static double EPS = 1.0e-8;


  static Point2D getDeltaXY(double hw, Point2D p1, Point2D p2, Point2D p3) {
    double alpha, beta, theta, r;
    Point2D pnt = new Point2D.Double();

    alpha = getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    beta = getAngle(p2.getX(), p2.getY(), p3.getX(), p3.getY());

    theta = (alpha + beta + Math.PI) /2.0;

    if (Math.abs(Math.cos((alpha - beta) /2.0)) < EPS) {
      log.error("Internal algorithm error: cos((alpha - beta)/2) = 0");
      return pnt;
    }
    r = ((double) hw) / Math.cos((alpha - beta)/2.0);
    pnt.setLocation(r * Math.cos(theta), r * Math.sin(theta));
    return pnt;
  }


  static Point2D getEndDeltaXY(double hw, Point2D p1, Point2D p2) {
    double alpha, theta, r;
    Point2D pnt = new Point2D.Double();

    alpha = getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());

    theta = alpha;
    r = hw;
    pnt.setLocation((-r * Math.sin(theta)), (r * Math.cos(theta)));

    return pnt;
  }

  
  private static double getAngle(double x1, double y1, double x2, double y2) {
    double angle;

    if(x1 == x2) {
      angle =  (Math.PI / 2.0) * ((y2 > y1) ? 1 : -1);
    }
    else {
      angle = Math.atan(Math.abs(y2 - y1) / Math.abs(x2 - x1));
      if(y2 >= y1) {
        if(x2 >= x1)
          angle += 0;
        else
          angle = Math.PI - angle;
      }
      else {
        if(x2 >= x1)
          angle = (2 * Math.PI) - angle;
        else
          angle += Math.PI;
      }
    }
    return angle;
  }


}
