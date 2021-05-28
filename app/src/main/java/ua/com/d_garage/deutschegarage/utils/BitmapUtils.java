package ua.com.d_garage.deutschegarage.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.util.Log;
import android.util.Size;
import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

public final class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    private BitmapUtils() {
        throw new UnsupportedOperationException();
    }

    public static Bitmap yuv420ToBitmap(ImageProxy imageProxy, Size previewSize) {

            Image image = imageProxy.getImage();

            if (image == null) {
                return null;
            }

            byte[] nv21 = yuv420ToNv21(image);

            YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 85, out);

            byte[] imageBytes = out.toByteArray();
            out.close();

            Bitmap tmp = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            float rotation = imageProxy.getImageInfo().getRotationDegrees();
            Size imageSize;
            if (image.getWidth() > image.getHeight()) {
                imageSize = new Size(image.getHeight(), image.getWidth());
            } else {
                imageSize = new Size(image.getWidth(), image.getHeight());
            }
            Matrix matrix = getTransformationMatrix(previewSize, imageSize, rotation);

            return Bitmap.createBitmap(tmp, 0, 0, tmp.getWidth(), tmp.getHeight(), matrix, true);
        } catch (Exception e){
            Log.e(TAG, "yuv420ToBitmap: failed convert to bitmap", e);
            return null;
        }
    }

    private static Matrix getTransformationMatrix(Size previewSize, Size imageSize, float degrees) {
        float viewAspectRatio = (float) previewSize.getWidth() / previewSize.getHeight();
        float imageAspectRatio = (float) imageSize.getWidth() / imageSize.getHeight();
        float postScaleWidthOffset = 0;
        float postScaleHeightOffset = 0;
        float scaleFactor;

        if (viewAspectRatio > imageAspectRatio) {
            scaleFactor = (float) previewSize.getWidth() / imageSize.getWidth();
            postScaleHeightOffset = ((float) previewSize.getWidth() / imageAspectRatio - previewSize.getHeight()) / 2;
        } else {
            scaleFactor = (float) previewSize.getHeight() / imageSize.getHeight();
            postScaleWidthOffset = ((float) previewSize.getHeight() * imageAspectRatio - previewSize.getWidth()) / 2;
        }

        Matrix transformationMatrix = new Matrix();
        transformationMatrix.setScale(scaleFactor, scaleFactor);
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset);
        transformationMatrix.postRotate(degrees);
        return transformationMatrix;
    }

    public static byte[] yuv420ToNv21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width * height;
        int uvSize = width * height / 4;

        byte[] nv21 = new byte[ySize + uvSize * 2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert (image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        } else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos < ySize; pos += width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }

        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert (rowStride == image.getPlanes()[1].getRowStride());
        assert (pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte) ~savePixel);
                if (uBuffer.get(0) == (byte) ~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            } catch (ReadOnlyBufferException ex) {
                // unfortunately, we cannot check if vBuffer and uBuffer overlap
            }

            // unfortunately, the check failed. We must save U and V pixel by pixel
            vBuffer.put(1, savePixel);
        }

        // other optimizations could check if (pixelStride == 1) or (pixelStride == 2),
        // but performance gain would be less significant

        for (int row = 0; row < height / 2; row++) {
            for (int col = 0; col < width / 2; col++) {
                int vuPos = col * pixelStride + row * rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }

        return nv21;
    }

}
