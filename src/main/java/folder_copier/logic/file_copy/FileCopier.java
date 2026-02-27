package folder_copier.logic.file_copy;

import java.io.IOException;
import java.nio.file.Path;

import folder_copier.ui.workers.ProgressAdder;

public interface FileCopier {

	void copy(Path src, Path dst, ProgressAdder progressable) throws IOException;
}
