package com.ubia.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore.Video.Thumbnails;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class AntsImageDownloader extends BaseImageDownloader {

	public AntsImageDownloader(Context context) {
		super(context);
	}

	@Override
	protected InputStream getStreamFromOtherSource(String imageUri, Object extra)
			throws IOException {
		String uri = imageUri.toString();
		String scheme = "video://";
		if (uri.startsWith(scheme)) {
			String filePath = uri.substring(scheme.length());
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath,
					Thumbnails.MINI_KIND);
			if (bitmap != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
				return new ByteArrayInputStream(bos.toByteArray());
			} else {
				return null;
			}
		}

		return super.getStreamFromOtherSource( imageUri , extra);
	}
}
