/*
 * Copyright 2008 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mockftpserver.fake.filesystem

/**
 * Implementation of the  {@link FileSystem}  interface that simulates a Unix
 * file system. The rules for file and directory names include: 
 * <ul>
 *   <li>Filenames are case-sensitive</li>
 *   <li>Forward slashes (/) are the only valid path separators</li>  
 * </ul>
 *
 * @version $Revision$ - $Date$
 *
 * @author Chris Mair
 */
class FakeUnixFileSystem extends AbstractFakeFileSystem {

    public static final String SEPARATOR = "/"

    /**
     * Construct a new instance and initialize the directoryListingFormatter to a UnixDirectoryListingFormatter.
     */
    FakeUnixFileSystem() {
        this.directoryListingFormatter = new UnixDirectoryListingFormatter()
    }

    //-------------------------------------------------------------------------
    // Abstract Method Implementations
    //-------------------------------------------------------------------------

    protected String getSeparator() {
        return SEPARATOR
    }

    protected boolean isValidName(String path) {
        assert path != null
        // Any character but '/'
        return path ==~ /\/|(\/[^\/]+)+/
    }

    /**
     * Return true if the specified char is a separator character ('\' or '/')
     * @param c - the character to test
     * @return true if the specified char is a separator character ('\' or '/')
     */
    protected boolean isSeparator(char c) {
        return c == SEPARATOR.charAt(0)
    }

    protected String componentsToPath(List components) {
        if (components.size() == 1) {
            def first = components[0]
            if (first == "" || isRoot(first)) {
                return first + this.separator
            }
        }
        return components.join(this.separator)
    }

    protected boolean isRoot(String pathComponent) {
        return pathComponent.contains(":")
    }

    /**
     * Return the components of the specified path as a List. The components are normalized, and
     * the returned List does not include path separator characters. 
     */
    protected List normalizedComponents(String path) {
        assert path != null
        def p = path.replace("/", this.separator)

        // TODO better way to do this
        if (p == this.separator) {
            return [""]
        }

        def parts = p.split("\\" + this.separator) as List
        def result = []
        parts.each {part ->
            if (part == "..") {
                result.remove(result.size() - 1)
            }
            else if (part != ".") {
                result << part
            }
        }
        return result
    }

    /**
     * Return true if the specified path designates an absolute file path. For Unix
     * paths, a path is absolute if it starts with the '/' character.
     *
     * @param path - the path
     * @return true if path is absolute, false otherwise
     *
     * @throws AssertionError - if path is null
     */
    boolean isAbsolute(String path) {
        return isValidName(path)
    }

}