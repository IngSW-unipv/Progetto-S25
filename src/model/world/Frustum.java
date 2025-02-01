package model.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Handles view frustum culling for efficient rendering of the 3D world
 * Maintains and updates the six planes that define the view frustum
 */
public class Frustum {
    /** Indices for the six frustum planes */
    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int NEAR = 4;
    private static final int FAR = 5;

    /** Array containing all six planes of the frustum */
    private final FrustumPlane[] planes;


    /**
     * Creates a new frustum with six uninitialized planes
     */
    public Frustum() {
        planes = new FrustumPlane[6];
        for (int i = 0; i < 6; i++) {
            planes[i] = new FrustumPlane();
        }
    }

    /**
     * Updates all frustum planes based on current view-projection matrix
     */
    public void update(Matrix4f projectionViewMatrix) {
        // Extract left plane coefficients
        planes[LEFT].normal.x = projectionViewMatrix.m03() + projectionViewMatrix.m00();
        planes[LEFT].normal.y = projectionViewMatrix.m13() + projectionViewMatrix.m10();
        planes[LEFT].normal.z = projectionViewMatrix.m23() + projectionViewMatrix.m20();
        planes[LEFT].distance = projectionViewMatrix.m33() + projectionViewMatrix.m30();

        // Extract right plane coefficients
        planes[RIGHT].normal.x = projectionViewMatrix.m03() - projectionViewMatrix.m00();
        planes[RIGHT].normal.y = projectionViewMatrix.m13() - projectionViewMatrix.m10();
        planes[RIGHT].normal.z = projectionViewMatrix.m23() - projectionViewMatrix.m20();
        planes[RIGHT].distance = projectionViewMatrix.m33() - projectionViewMatrix.m30();

        // Extract bottom plane coefficients
        planes[BOTTOM].normal.x = projectionViewMatrix.m03() + projectionViewMatrix.m01();
        planes[BOTTOM].normal.y = projectionViewMatrix.m13() + projectionViewMatrix.m11();
        planes[BOTTOM].normal.z = projectionViewMatrix.m23() + projectionViewMatrix.m21();
        planes[BOTTOM].distance = projectionViewMatrix.m33() + projectionViewMatrix.m31();

        // Extract top plane coefficients
        planes[TOP].normal.x = projectionViewMatrix.m03() - projectionViewMatrix.m01();
        planes[TOP].normal.y = projectionViewMatrix.m13() - projectionViewMatrix.m11();
        planes[TOP].normal.z = projectionViewMatrix.m23() - projectionViewMatrix.m21();
        planes[TOP].distance = projectionViewMatrix.m33() - projectionViewMatrix.m31();

        // Extract near plane coefficients
        planes[NEAR].normal.x = projectionViewMatrix.m03() + projectionViewMatrix.m02();
        planes[NEAR].normal.y = projectionViewMatrix.m13() + projectionViewMatrix.m12();
        planes[NEAR].normal.z = projectionViewMatrix.m23() + projectionViewMatrix.m22();
        planes[NEAR].distance = projectionViewMatrix.m33() + projectionViewMatrix.m32();

        // Extract far plane coefficients
        planes[FAR].normal.x = projectionViewMatrix.m03() - projectionViewMatrix.m02();
        planes[FAR].normal.y = projectionViewMatrix.m13() - projectionViewMatrix.m12();
        planes[FAR].normal.z = projectionViewMatrix.m23() - projectionViewMatrix.m22();
        planes[FAR].distance = projectionViewMatrix.m33() - projectionViewMatrix.m32();

        // Normalize all plane equations
        for (FrustumPlane plane : planes) {
            float length = (float) Math.sqrt(plane.normal.x * plane.normal.x +
                    plane.normal.y * plane.normal.y +
                    plane.normal.z * plane.normal.z);
            plane.normal.div(length);
            plane.distance /= length;
        }
    }

    /**
     * Checks if a cube is inside the frustum
     * @param x Center x coordinate
     * @param y Center y coordinate
     * @param z Center z coordinate
     * @param size Length of cube sides
     */
    public boolean isBoxInFrustum(float x, float y, float z, float size) {
        float halfSize = size / 2;
        return isAABBInFrustum(
                x - halfSize, y - halfSize, z - halfSize,
                x + halfSize, y + halfSize, z + halfSize
        );
    }

    /**
     * Tests if an axis-aligned bounding box intersects the frustum
     * Uses p-vertex and n-vertex optimization for efficient testing
     */
    public boolean isAABBInFrustum(float minX, float minY, float minZ,
                                   float maxX, float maxY, float maxZ) {
        for (FrustumPlane plane : planes) {
            Vector3f normal = plane.normal;

            // Select vertices based on plane normal
            float pVertex_x = (normal.x >= 0) ? maxX : minX;
            float pVertex_y = (normal.y >= 0) ? maxY : minY;
            float pVertex_z = (normal.z >= 0) ? maxZ : minZ;

            float nVertex_x = (normal.x >= 0) ? minX : maxX;
            float nVertex_y = (normal.y >= 0) ? minY : maxY;
            float nVertex_z = (normal.z >= 0) ? minZ : maxZ;

            // Test positive vertex for rejection
            if ((normal.x * pVertex_x + normal.y * pVertex_y +
                    normal.z * pVertex_z + plane.distance) < 0) {
                return false;
            }

            // Test negative vertex for early acceptance
            if ((normal.x * nVertex_x + normal.y * nVertex_y +
                    normal.z * nVertex_z + plane.distance) < 0) {
                continue;
            }

            return true;
        }
        return true;
    }

    /**
     * Tests if a chunk intersects the frustum
     */
    public boolean isChunkInFrustum(Vector3f chunkPos, int chunkSize) {
        // Convert chunk coordinates to world space
        float minX = chunkPos.x * chunkSize;
        float minY = chunkPos.y * chunkSize;
        float minZ = chunkPos.z * chunkSize;
        float maxX = minX + chunkSize;
        float maxY = minY + chunkSize;
        float maxZ = minZ + chunkSize;

        return isAABBInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /**
     * Represents a plane in the frustum defined by normal and distance
     */
    private static class FrustumPlane {
        Vector3f normal;
        float distance;

        FrustumPlane() {
            normal = new Vector3f();
            distance = 0;
        }
    }
}