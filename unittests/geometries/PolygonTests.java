package geometries;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing Polygons
 * @author Dan
 */
public class PolygonTests {
   /**
    * Delta value for accuracy when comparing the numbers of type 'double' in
    * assertEquals
    */
   private final double DELTA = 0.000001;

   /** Test method for {@link geometries.Polygon#Polygon(primitives.Point...)}. */
   @Test
   public void testConstructor() {
      // ============ Equivalence Partitions Tests ==============

      // TC01: Correct concave quadrangular with vertices in correct order
      assertDoesNotThrow(() -> new Polygon(new Point(0, 0, 1),
                                           new Point(1, 0, 0),
                                           new Point(0, 1, 0),
                                           new Point(-1, 1, 1)),
                         "Failed constructing a correct polygon");

      // TC02: Wrong vertices order
      assertThrows(IllegalArgumentException.class, //
                   () -> new Polygon(new Point(0, 0, 1),
                           new Point(0, 1, 0),
                           new Point(1, 0, 0),
                           new Point(-1, 1, 1)), //
                   "Constructed a polygon with wrong order of vertices");

      // TC03: Not in the same plane
      assertThrows(IllegalArgumentException.class, //
                   () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 2, 2)), //
                   "Constructed a polygon with vertices that are not in the same plane");

      // TC04: Concave quadrangular
      assertThrows(IllegalArgumentException.class, //
                   () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                                     new Point(0.5, 0.25, 0.5)), //
                   "Constructed a concave polygon");

      // =============== Boundary Values Tests ==================

      // TC10: Vertex on a side of a quadrangular
      assertThrows(IllegalArgumentException.class, //
                   () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
                                     new Point(0, 0.5, 0.5)),
                   "Constructed a polygon with vertix on a side");

      // TC11: Last point = first point
      assertThrows(IllegalArgumentException.class, //
                   () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
                   "Constructed a polygon with vertice on a side");

      // TC12: Co-located points
      assertThrows(IllegalArgumentException.class, //
                   () -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 1, 0)),
                   "Constructed a polygon with vertice on a side");

   }

   /** Test method for {@link geometries.Polygon#getNormal(primitives.Point)}. */
   @Test
   public void testGetNormal() {
      // ============ Equivalence Partitions Tests ==============
      // TC01: There is a simple single test here - using a quad
      Point[] pts =
         { new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(-1, 1, 1) };
      Polygon pol = new Polygon(pts);
      // ensure there are no exceptions
      assertDoesNotThrow(() -> pol.getNormal(new Point(0, 0, 1)), "");
      // generate the test result
      Vector result = pol.getNormal(new Point(0, 0, 1));
      // ensure |result| = 1
      assertEquals(1, result.length(), DELTA, "Polygon's normal is not a unit vector");
      // ensure the result is orthogonal to all the edges
      for (int i = 0; i < 3; ++i)
         assertEquals(0d, result.dotProduct(pts[i].subtract(pts[i == 0 ? 3 : i - 1])), DELTA,
                      "Polygon's normal is not orthogonal to one of the edges");
   }

   @Test
   void testFindIntersections()
   {
      Point[] pts = { new Point(0, 0, 3), new Point(-3, 3, 3), new Point(0, 3, 0), new Point(3, 0, 0) };
      Polygon pol = new Polygon(pts);

      // ============ Equivalence Partitions Tests ==============
      // TC01: Ray intersects the polygon at a point
      Point p = new Point(-1, 0, 0);
      Vector v = new Vector(1, 1, 2);
      Ray ray = new Ray(p, v);

      List<Point> expected = new ArrayList<>();
      expected.add(new Point(0, 1, 2));

      List<Point> result = pol.findIntersections(ray);

      assertEquals(expected, result, "Failed to find intersection point");

      // TC02: Ray does not intersect the polygon, goes beyond max distance
      p = new Point(-1, 0, 0);
      v = new Vector(1, 1, 8);
      ray = new Ray(p, v);

      expected = new ArrayList<>();
      expected.clear();

      result = pol.findIntersections(ray);

      assertNull(result, "Failed to find intersection point");

      // =============== Boundary Values Tests ==================
      // TC11: Ray misses the polygon completely
      p = new Point(-1, -1, 0);
      v = new Vector(1, 1, 8);
      ray = new Ray(p, v);

      expected = new ArrayList<>();
      expected.clear();

      result = pol.findIntersections(ray);

      assertNull(result, "Failed to find intersection point");

      // TC12: Ray is parallel to the polygon and does not intersect
      p = new Point(-1, 0, 0);
      v = new Vector(2, -1, 0);
      ray = new Ray(p, v);

      expected = new ArrayList<>();
      expected.clear();

      result = pol.findIntersections(ray);

      assertNull(result, "Failed to find intersection point");

      // TC13: Ray is parallel to the polygon but starts outside the plane of the polygon
      p = new Point(-1, 0, 0);
      v = new Vector(3, 3, 0);
      ray = new Ray(p, v);

      expected = new ArrayList<>();
      expected.clear();

      result = pol.findIntersections(ray);

      assertNull(result, "Failed to find intersection point");

      // TC14: Ray intersects the plane but outside the polygon boundaries
      p = new Point(-1, 0, 0);
      v = new Vector(1, 0, 3);
      ray = new Ray(p, v);

      expected = new ArrayList<>();
      expected.clear();

      result = pol.findIntersections(ray);

      assertNull(result, "Failed to find intersection point");

   }

//   @Test
//   void testFindIntersectionsWithDistance() {
//      // Define the polygon for testing
//      Polygon polygon = new Polygon(
//              new Point(0, 1, 0),
//              new Point(1, 1, 0),
//              new Point(1, 0, 0),
//              new Point(-1, -1, 0)
//      );
//
//      // Test case 01: Ray intersects the polygon within a very large distance
//      List<Point> result = polygon.findIntersections(new Ray(new Point(1, 1, 1), new Vector(-0.5, -0.5, -2)), 500);
//      assertEquals(1, result.size(), "Error: Ray should intersect the polygon within the distance (TC01)");
//
//      // Test case 02: Ray does not intersect the polygon within a very small distance
//      result = polygon.findIntersections(new Ray(new Point(1, 1, 1), new Vector(-0.5, -0.5, -2)), 1);
//      assertNull(result, "Error: Ray should not intersect the polygon within the distance (TC02)");
//   }


}
