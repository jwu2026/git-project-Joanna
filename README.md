# git-project-Joanna
GP-2.1.1: The method createFiles programmatically creates and writes to two files. The verify method checks if the files and directories exist. The deleteGit method deletes the created files and directories. Multiple initialization, verify and reset cycles are run in the main to ensure reliability. An edge case is when git/ exists, newRepo method doesn't create it again.

GP-2.2: The method generateHash reads in a file as string and returns a SHA-1 hash string of the text.

GP-2.3: The method createBlob generates a SHA-1 hash of the file's content, then creates a new file in git/objects and then stores an exact copy of the original file's content in the new blob file.

GP-2.3.1: The verifyBlob method checks if a file exists in git/objects and cleanupBlob method deletes all files in the directory.

GP-2.3.2: The compression method uses a Deflater to compress data and returns byte array.

GP-2.4: The updateIndex method adds BLOB and file entries to the index.

GP-2.4.1: The indexTester method creates text files, blobs them and adds entries to index file. It checks if each blob exists, the index isn’t empty and verifies that the index entries match the actual files.

GP-2.4.2: The cleanup method removes all created text files, deletes the blob files stored in the git/objects and deletes entries in git/index file.

GP-3.1: The modified updateIndex method changes the index so it now saves the relative path of each file instead of just the name. It also prevents adding the same file twice if nothing changed. If a file was edited, the method replaces the old hash with the new one. If the file is new, it adds it to the index in the correct format.

GP-3.2: The createTree method creates a blob for each file and if it encounters a folder, it recursively calls itself so the subtrees are built. After going through all items, the method puts the contents of the tree into a temporary file, hashes it, stores the tree object in git/objects and returns the hash.

GP-3.3: The treeIndex method turns the index into a working list and creates tree entries for each folder as it goes. It keeps shrinking the list until only the root is left, then writes out the final root tree.

GP-4.1: The program stages the files correctly and generates blobs and trees based on the index. The root tree file is manually traced and all hashes are verified to ensure that each staged blob is in its correct location within the trees and that the corresponding blob files exist in the objects directory.

GP-4.2: The commit method creates a commit file pointing to the root tree and records author, date, and message for the current repository state. Also adds the previous commit’s hash so all commits are linked together in order.