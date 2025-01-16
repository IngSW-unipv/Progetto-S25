package model;

import org.joml.Matrix4f;
import org.joml.Vector4f;

/**
 * Represents a view frustum used for frustum culling in 3D rendering.
 * It defines six planes (left, right, bottom, top, near, far) that form the frustum.
 */
public class Frustum {
    private final Vector4f[] planes; // Left, Right, Bottom, Top, Near, Far

    /**
     * Constructs a new Frustum object with uninitialized planes.
     */
    public Frustum() {
        planes = new Vector4f[6];
        for (int i = 0; i < 6; i++) {
            planes[i] = new Vector4f();
        }
    }

    /**
     * Updates the frustum planes based on the provided projection-view matrix.
     * This method calculates the six planes that define the frustum.
     *
     * @param projectionViewMatrix The combined projection-view matrix.
     */
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
            plane.div(length); // Normalize the plane equation
        }
    }

    /**
     * Checks if a box is within the frustum.
     * This method tests if any part of the box is inside the frustum.
     *
     * @param x    The x-coordinate of the box's center.
     * @param y    The y-coordinate of the box's center.
     * @param z    The z-coordinate of the box's center.
     * @param size The size of the box (assumed to be a cube).
     * @return True if the box is inside the frustum, false otherwise.
     */
    public boolean isBoxInFrustum(float x, float y, float z, float size) {
        // Corners of the box
        float[] corners = {
                x - size / 2, x + size / 2,
                y - size / 2, y + size / 2,
                z - size / 2, z + size / 2
        };

        // Check if the box intersects with all frustum planes
        for (Vector4f plane : planes) {
            boolean anyPointIn = false;
            for (int i = 0; i < 8; i++) {
                // Determine the corner position based on bitmasking
                float px = (i & 1) == 0 ? corners[0] : corners[1];
                float py = (i & 2) == 0 ? corners[2] : corners[3];
                float pz = (i & 4) == 0 ? corners[4] : corners[5];

                // If any corner is inside the plane, the box intersects the frustum
                if (plane.x * px + plane.y * py + plane.z * pz + plane.w >= 0) {
                    anyPointIn = true;
                    break;
                }
            }
            if (!anyPointIn) return false; // If no corner is inside, return false
        }
        return true; // The box is inside the frustum
    }
}
