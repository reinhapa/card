package card;

import java.awt.Component;
import java.io.*;
import java.nio.file.Paths;
import java.util.stream.IntStream;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import static java.nio.file.Files.*;

public class StoryMania {
    public static void main(String[] args) throws IOException {
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("StoryMania .smp and .mp3",
                new String[]{"mp3", "smp"});
        fc.setFileFilter(filter);
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(0);
        fc.setMultiSelectionEnabled(true);
        fc.setDialogType(0);
        fc.setDialogTitle("Select all files to convert");
        int res = fc.showDialog((Component) null, "Start convert");
        File[] files = fc.getSelectedFiles();
        if (res == 0) {
            for (File in : files) {
                if (in.exists()) {
                    String outName;
                    if (in.getName().endsWith(".mp3")) {
                        outName = in.getAbsolutePath().substring(0, in.getAbsolutePath().length() - 4) + ".smp";
                    } else {
                        outName = in.getAbsolutePath().substring(0, in.getAbsolutePath().length() - 4) + ".mp3";
                    }
                    try (InputStream fin = newInputStream(in.toPath());
                         OutputStream fout = newOutputStream(Paths.get(outName));
                         StoryManiaInputStream sin = new StoryManiaInputStream(fin)) {
                        int read;
                        byte[] b = new byte[1024];
                        while ((read = sin.read(b)) != -1) {
                            fout.write(b, 0, read);
                        }
                    }
                }
            }
        }
    }
}