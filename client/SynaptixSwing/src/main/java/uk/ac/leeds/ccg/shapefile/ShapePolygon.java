package uk.ac.leeds.ccg.shapefile;

import java.io.*;
import cmp.LEDataStream.*;

/**
 * Wrapper for a Shapefile polygon.
 */
public class ShapePolygon extends ShapeArc implements Serializable {

  public ShapePolygon( LEDataInputStream file )
    throws IOException, InvalidShapefileException { 

    file.setLittleEndianMode(true);
    int shapeType = file.readInt();
    if ( shapeType != Shapefile.POLYGON ) {
      throw new InvalidShapefileException
        ("Error: Attempt to load non polygon shape as polygon."); //$NON-NLS-1$
    }

    for ( int i = 0; i<4; i++ ) {
      box[i] = file.readDouble();
    }

    numParts = file.readInt();
    numPoints = file.readInt();

    parts = new int[numParts];
      
    for(int i = 0;i<numParts;i++){
      parts[i]=file.readInt();
    }
      
    for ( int i = 0; i<numPoints; i++ ) {
      double x = file.readDouble();
      double y = file.readDouble();
        
      setPoint( i, x, y );
    }
      
  }
    
  public ShapePolygon(double[] box,int[] parts,ShapePoint[] points){
    super( box, parts, points );
  }
    
  public int getShapeType(){
    return Shapefile.POLYGON;
  }
  public int getLength(){
    return (22+(2*numParts)+numPoints*8);
  }

}

/*
 * $Log: ShapePolygon.java,v $
 * Revision 1.5  2001/08/01 12:33:29  ianturton
 * modification submited by Michael Becke <becke@u.washington.edu> to reduce
 * memory usage.
 *
 *
 *
 */
