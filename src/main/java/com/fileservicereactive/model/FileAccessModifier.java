package com.fileservicereactive.model;

import java.nio.file.Path;

public enum FileAccessModifier {
    PUBLIC,PRIVATE;

    private Path imagePath;

    private Path documentPath;

    FileAccessModifier() {
    }

    FileAccessModifier(Path documentPath) {
        this.documentPath = documentPath;
    }


    public Path getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(Path documentPath) {
        this.documentPath = documentPath;
    }
}
