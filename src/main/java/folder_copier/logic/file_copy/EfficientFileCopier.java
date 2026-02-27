package folder_copier.logic.file_copy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import folder_copier.ui.workers.ProgressAdder;

public class EfficientFileCopier implements FileCopier {

	@Override
	public void copy(Path src, Path dst, ProgressAdder progressable) throws IOException {
		Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
	}
}
