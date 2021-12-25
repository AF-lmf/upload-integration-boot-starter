package com.lmf.utils;

import com.lmf.exception.ImageFormatException;
import lombok.extern.slf4j.Slf4j;
import net.sf.image4j.codec.ico.ICODecoder;
import org.springframework.lang.NonNull;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * @author lmf.
 * @title: ImageUtils
 * @description: TODO
 * @date 2021/12/25 0:45
 */
@Slf4j
public class ImageUtils {

    public static final String EXTENSION_ICO = "ico";

    public static BufferedImage getImageFromFile(InputStream is, String extension)
        throws IOException {
        log.debug("Current File type is : [{}]", extension);

        if (EXTENSION_ICO.equals(extension)) {
            try {
                return ICODecoder.read(is).get(0);
            } catch (IOException e) {
                throw new ImageFormatException("ico 文件已损坏", e);
            }
        } else {
            return ImageIO.read(is);
        }
    }

    @NonNull
    public static ImageReader getImageReaderFromFile(InputStream is, String formatName)
        throws IOException {
        try {
            Iterator<ImageReader> readerIterator = ImageIO.getImageReadersByFormatName(formatName);
            ImageReader reader = readerIterator.next();
            ImageInputStream stream = ImageIO.createImageInputStream(is);
            ImageIO.getImageReadersByFormatName(formatName);
            reader.setInput(stream, true);
            return reader;
        } catch (Exception e) {
            throw new IOException("Failed to read image reader.", e);
        }
    }
}
