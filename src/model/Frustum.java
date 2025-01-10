package model;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class Frustum {
    private final Vector4f[] planes; // Left, Right, Bottom, Top, Near, Far

    public Frustum() {
        planes = new Vector4f[6];
        for (int i = 0; i < 6; i++) {
            planes[i] = new Vector4f();
        }
    }

    public void update(Matrix4f projectionViewMatrix) {
        // Left plane
        planes[0].x = projectionViewMatrix.m03() + projectionViewMatrix.m00();
        planes[0].y = projectionViewMatrix.m13() + projectionViewMatrix.m10();
        planes[0].z = projectionViewMatrix.m23() + projectionViewMatrix.m20();
        planes[0].w = projectionViewMatrix.m33() + projectionViewMatrix.m30();

        // Right plane
        planes[1].x = projectionViewMatrix.m03() - projectionViewMatrix.m00();
        planes[1].y = projectionViewMatrix.m13() - projectionViewMatrix.m10();
        planes[1].z = projectionViewMatrix.m23() - projectionViewMatrix.m20();
        planes[1].w = projectionViewMatrix.m33() - projectionViewMatrix.m30();

        // Bottom plane
        planes[2].x = projectionViewMatrix.m03() + projectionViewMatrix.m01();
        planes[2].y = projectionViewMatrix.m13() + projectionViewMatrix.m11();
        planes[2].z = projectionViewMatrix.m23() + projectionViewMatrix.m21();
        planes[2].w = projectionViewMatrix.m33() + projectionViewMatrix.m31();

        // Top plane
        planes[3].x = projectionViewMatrix.m03() - projectionViewMatrix.m01();
        planes[3].y = projectionViewMatrix.m13() - projectionViewMatrix.m11();
        planes[3].z = projectionViewMatrix.m23() - projectionViewMatrix.m21();
        planes[3].w = projectionViewMatrix.m33() - projectionViewMatrix.m31();

        // Near plane
        planes[4].x = projectionViewMatrix.m03() + projectionViewMatrix.m02();
        planes[4].y = projectionViewMatrix.m13() + projectionViewMatrix.m12();
        planes[4].z = projectionViewMatrix.m23() + projectionViewMatrix.m22();
        planes[4].w = projectionViewMatrix.m33() + projectionViewMatrix.m32();

        // Far plane
        planes[5].x = projectionViewMatrix.m03() - projectionViewMatrix.m02();
        planes[5].y = projectionViewMatrix.m13() - projectionViewMatrix.m12();
        planes[5].z = projectionViewMatrix.m23() - projectionViewMatrix.m22();
        planes[5].w = projectionViewMatrix.m33() - projectionViewMatrix.m32();

        // Normalize planes
        for (Vector4f plane : planes) {
            float length = (float) Math.sqrt(plane.x * plane.x + plane.y * plane.y + plane.z * plane.z);
            plane.div(length);
        }
    }

    public boolean isBoxInFrustum(float x, float y, float z, float size) {
        // Per ogni vertice del box
        float[] corners = {
            x - size/2, x + size/2,
            y - size/2, y + size/2,
            z - size/2, z + size/2
        };

        for (Vector4f plane : planes) {
            boolean anyPointIn = false;
            for (int i = 0; i < 8; i++) {
                float px = (i & 1) == 0 ? corners[0] : corners[1];
                float py = (i & 2) == 0 ? corners[2] : corners[3];
                float pz = (i & 4) == 0 ? corners[4] : corners[5];

                if (plane.x * px + plane.y * py + plane.z * pz + plane.w >= 0) {
                    anyPointIn = true;
                    break;
                }
            }
            if (!anyPointIn) return false;
        }
        return true;
    }
}