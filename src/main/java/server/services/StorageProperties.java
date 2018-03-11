package server.services;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageProperties {

    private String location;
    private Path path;

    public StorageProperties(String location) {
        this.location = location;
        this.path = Paths.get(this.location);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Path getPath() { return path; }

    public void setPath(Path path) { this.path = path; }

}
