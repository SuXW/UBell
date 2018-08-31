package com.ubia.vr;

  
public class SphereMath {

    public static class Point3F {
        public float x;
        public float y;
        public float z;

        public Point3F() {
            x = 0;
            y = 0;
            z = 0;
        }

        public Point3F(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point3F normalize() {
            float norm = (float) Math.sqrt(x * x + y * y + z * z);
            return new Point3F(x / norm, y / norm, z / norm);
        }
    }

    public static Point3F cross(Point3F u, Point3F v) {
        Point3F uv = new Point3F();
        uv.x = u.y * v.z - v.y * u.z;
        uv.y = u.z * v.x - v.z * u.x;
        uv.z = u.x * v.y - v.x * u.y;

        return uv;
    }

    public static Point3F add(Point3F u, Point3F v) {
        Point3F uv = new Point3F();
        uv.x = u.x + v.x;
        uv.y = u.y + v.y;
        uv.z = u.z + v.z;

        return uv;
    }

    public static Point3F mul(Point3F u, float k) {
        Point3F ku = new Point3F();
        ku.x = u.x * k;
        ku.y = u.y * k;
        ku.z = u.z * k;

        return ku;
    }



}
