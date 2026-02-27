package folder_copier.logic.file_copy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;

import folder_copier.ui.workers.ProgressAdder;

public class ProgressableFileCopier implements FileCopier {

	@Override
	public void copy(Path src, Path dst, ProgressAdder progressable) throws IOException {
		try {
        	File srcFile = src.toFile();
            InputStream in = new FileInputStream(srcFile);
            OutputStream out = new FileOutputStream(dst.toFile());
            BigDecimal expectedBytes = new BigDecimal(srcFile.length());
            long copiedBytes = 0;
            BigDecimal previousProgress = null, progress;
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                copiedBytes += len;
                progress = new BigDecimal(copiedBytes).divide(expectedBytes, 2, RoundingMode.HALF_EVEN);
                if (previousProgress == null || progress.compareTo(previousProgress) > 0) {
                	progressable.addRatio(progress);
                }
                previousProgress = progress;
            }
            System.out.println("");
            in.close();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
}
