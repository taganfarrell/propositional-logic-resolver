import java.io.*;

public class Utils {

  /**
   * http://alvinalexander.com/java/java-deep-clone-example-source-code
   */

  public static Object deepClone( Object object ) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream( baos );
      oos.writeObject( object );
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream( bais );
      return ois.readObject();
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  } // Utils::deepClone

} // Utils class
