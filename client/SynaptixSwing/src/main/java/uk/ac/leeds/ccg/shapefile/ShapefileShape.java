package uk.ac.leeds.ccg.shapefile;

import cmp.LEDataStream.LEDataOutputStream;

public interface ShapefileShape {
    public int getShapeType();
    public void write(LEDataOutputStream file) throws java.io.IOException;
    public int getLength(); //length in 16bit words
}
