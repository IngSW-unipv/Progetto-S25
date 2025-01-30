package model.world;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Frustum {
    private final FrustumPlane[] planes;
    private static final int TOP = 0;
    private static final int BOTTOM = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    private static final int NEAR = 4;
    private static final int FAR = 5;

    public Frustum() {
        planes = new FrustumPlane[6];
        for (int i = 0; i < 6; i++) {
            planes[i] = new FrustumPlane();
        }
    }

    public void update(Matrix4f projectionViewMatrix) {
        // Left plane
        planes[LEFT].normal.x = projectionViewMatrix.m03() + projectionViewMatrix.m00();
        planes[LEFT].normal.y = projectionViewMatrix.m13() + projectionViewMatrix.m10();
        planes[LEFT].normal.z = projectionViewMatrix.m23() + projectionViewMatrix.m20();
        planes[LEFT].distance = projectionViewMatrix.m33() + projectionViewMatrix.m30();

        // Right plane
        planes[RIGHT].normal.x = projectionViewMatrix.m03() - projectionViewMatrix.m00();
        planes[RIGHT].normal.y = projectionViewMatrix.m13() - projectionViewMatrix.m10();
        planes[RIGHT].normal.z = projectionViewMatrix.m23() - projectionViewMatrix.m20();
        planes[RIGHT].distance = projectionViewMatrix.m33() - projectionViewMatrix.m30();

        // Bottom plane
        planes[BOTTOM].normal.x = projectionViewMatrix.m03() + projectionViewMatrix.m01();
        planes[BOTTOM].normal.y = projectionViewMatrix.m13() + projectionViewMatrix.m11();
        planes[BOTTOM].normal.z = projectionViewMatrix.m23() + projectionViewMatrix.m21();
        planes[BOTTOM].distance = projectionViewMatrix.m33() + projectionViewMatrix.m31();

        // Top plane
        planes[TOP].normal.x = projectionViewMatrix.m03() - projectionViewMatrix.m01();
        planes[TOP].normal.y = projectionViewMatrix.m13() - projectionViewMatrix.m11();
        planes[TOP].normal.z = projectionViewMatrix.m23() - projectionViewMatrix.m21();
        planes[TOP].distance = projectionViewMatrix.m33() - projectionViewMatrix.m31();

        // Near plane
        planes[NEAR].normal.x = projectionViewMatrix.m03() + projectionViewMatrix.m02();
        planes[NEAR].normal.y = projectionViewMatrix.m13() + projectionViewMatrix.m12();
        planes[NEAR].normal.z = projectionViewMatrix.m23() + projectionViewMatrix.m22();
        planes[NEAR].distance = projectionViewMatrix.m33() + projectionViewMatrix.m32();

        // Far plane
        planes[FAR].normal.x = projectionViewMatrix.m03() - projectionViewMatrix.m02();
        planes[FAR].normal.y = projectionViewMatrix.m13() - projectionViewMatrix.m12();
        planes[FAR].normal.z = projectionViewMatrix.m23() - projectionViewMatrix.m22();
        planes[FAR].distance = projectionViewMatrix.m33() - projectionViewMatrix.m32();

        // Normalize all planes
        for (FrustumPlane plane : planes) {
            float length = (float) Math.sqrt(plane.normal.x * plane.normal.x +
                    plane.normal.y * plane.normal.y +
                    plane.normal.z * plane.normal.z);
            plane.normal.div(length);
            plane.distance /= length;
        }
    }

    public boolean isBoxInFrustum(float x, float y, float z, float size) {
        return isAABBInFrustum(
                x - size/2, y - size/2, z - size/2,
                x + size/2, y + size/2, z + size/2
        );
    }

    public boolean isAABBInFrustum(float minX, float minY, float minZ,
                                   float maxX, float maxY, float maxZ) {
        for (FrustumPlane plane : planes) {
            Vector3f normal = plane.normal;

            float pVertex_x = (normal.x >= 0) ? maxX : minX;
            float pVertex_y = (normal.y >= 0) ? maxY : minY;
            float pVertex_z = (normal.z >= 0) ? maxZ : minZ;

            float nVertex_x = (normal.x >= 0) ? minX : maxX;
            float nVertex_y = (normal.y >= 0) ? minY : maxY;
            float nVertex_z = (normal.z >= 0) ? minZ : maxZ;

            // Test p-vertex
            if ((normal.x * pVertex_x + normal.y * pVertex_y +
                    normal.z * pVertex_z + plane.distance) < 0) {
                return false;
            }

            // Test n-vertex (optional, provides early acceptance)
            if ((normal.x * nVertex_x + normal.y * nVertex_y +
                    normal.z * nVertex_z + plane.distance) < 0) {
                continue;
            }

            // If we reach here, this plane is crossed by the AABB
            return true;
        }
        return true;
    }

    public boolean isChunkInFrustum(Vector3f chunkPos, int chunkSize) {
        float minX = chunkPos.x * chunkSize;
        float minY = chunkPos.y * chunkSize;
        float minZ = chunkPos.z * chunkSize;
        float maxX = minX + chunkSize;
        float maxY = minY + chunkSize;
        float maxZ = minZ + chunkSize;

        return isAABBInFrustum(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static class FrustumPlane {
        Vector3f normal;
        float distance;

        FrustumPlane() {
            normal = new Vector3f();
            distance = 0;
        }
    }
}