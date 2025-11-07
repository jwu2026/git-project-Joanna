# git-project-Joanna
GP-2.1.1: The method createFiles programmatically creates and writes to two files. The verify method checks if the files and directories exist. The deleteGit method deletes the created files and directories. Multiple initialization, verify and reset cycles are run in the main to ensure reliability. An edge case is when git/ exists, newRepo method doesn't create it again.

GP-2.2: The method generateHash reads in a file as string and returns a SHA-1 hash string of the text.

GP-2.3: The method createBlob generates a SHA-1 hash of the file's content, then creates a new file in git/objects and then stores an exact copy of the original file's content in the new blob file.

GP-2.3.1: The verifyBlob method checks if a file exists in git/objects and cleanupBlob method deletes all files in the directory.

GP-2.3.2: The compression method uses a Deflater to compress data and returns byte array.