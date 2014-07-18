package redis.tags;

import java.io.File;
import java.io.IOException;

public interface JHandler {
	void onJavaFile(File file) throws IOException;
}
