import java.io.*;

public class example implements Serializable {
    int i = 6;

    // Java requirements
    private void writeObject(java.io.ObjectOutputStream out)
        throws IOException {
	out.writeInt(i);
    }
    private void readObject(java.io.ObjectInputStream in)
        throws IOException, ClassNotFoundException {
	i = in.readInt();
    }

    // Koala XML serialization requirements
    public static void readObject(fr.dyade.koala.serialization.GeneratorInputStream in)
        throws IOException, ClassNotFoundException {
	in.readInt();
    }
}
