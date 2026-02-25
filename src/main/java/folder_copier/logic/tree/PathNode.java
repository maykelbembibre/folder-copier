package folder_copier.logic.tree;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class that represents a path node which can have children of the same class,
 * thus creating a tree of path nodes that can be represented as a string.
 */
public class PathNode {

	private final String name;
	private final List<PathNode> children = new ArrayList<>();
	
	/**
	 * Creates a new path node with the given name.
	 * @param name The name.
	 */
	public PathNode(String name) {
		this.name = name;
	}

	/**
	 * Returns a tree representation of this path node.
	 * @return A tree representation of this path node.
	 */
	public String getAsTree() {
		return this.getAsTreeRecursive("", "");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PathNode other = (PathNode) obj;
		return Objects.equals(name, other.name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Adds a relative path to this path node.
	 * @param relativePath A relative path.
	 */
	protected void addPath(Path relativePath) {
		Path relativePathFirst = relativePath.subpath(0, 1);
		PathNode child = this.addChild(relativePathFirst.toString());
		if (!relativePath.equals(relativePathFirst)) {
			Path relativePathRest = relativePathFirst.relativize(relativePath);
			child.addPath(relativePathRest);
		}
	}
	
	private PathNode addChild(String name) {
		PathNode possibleChild = new PathNode(name);
		PathNode result;
		int index = this.children.indexOf(possibleChild);
		if (index >= 0) {
			result = this.children.get(index);
		} else {
			this.children.add(possibleChild);
			result = possibleChild;
		}
		return result;
	}
	
	private String getAsTreeRecursive(String currentPrefix, String nextPrefix) {
		StringBuilder result = new StringBuilder(currentPrefix);
		result.append(this.name);
		result.append("\n");
		String childCurrentPrefix, childNextPrefix;
		int index = 0;
		for (PathNode child : this.children) {
			if (index == this.children.size() - 1) {
				childCurrentPrefix = "└─";
				childNextPrefix = "  ";
			} else {
				childCurrentPrefix = "├─";
				childNextPrefix = "│ ";
			}
			result.append(nextPrefix);
			result.append(child.getAsTreeRecursive(childCurrentPrefix, nextPrefix + childNextPrefix));
			index++;
		}
		return result.toString();
	}
}
