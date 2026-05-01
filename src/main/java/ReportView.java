import javax.swing.*;
import java.awt.*;

public class ReportView extends JFrame {
    private final JButton stopBtn  = new JButton("Stop");
    private final JButton startBtn  = new JButton("Start");
    private final JTextArea out    = new JTextArea(20, 50);

    private String dir;
    private long maxFS;
    private int bands;

    private ReportController controller;

    public ReportView() {
        super("FSStat — Virtual Threads");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Dir:"));
        JTextField dirField = new JTextField(System.getProperty("user.home"), 25);
        top.add(dirField);
        top.add(new JLabel("MaxFS:"));
        JTextField maxFsField = new JTextField("10000000", 10);
        top.add(maxFsField);
        top.add(new JLabel("Bands:"));
        JTextField bandsField = new JTextField("10", 4);
        top.add(bandsField);
        top.add(stopBtn);
        stopBtn.setEnabled(false);
        top.add(startBtn);
        startBtn.setEnabled(true);
        out.setEditable(false); out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        out.setText("");

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(out), BorderLayout.CENTER);

        stopBtn.addActionListener(_ -> onStop());
        startBtn.addActionListener(_ -> {
            this.dir = dirField.getText();
            this.maxFS = Long.parseLong(maxFsField.getText());
            this.bands = Integer.parseInt(bandsField.getText());
            stopBtn.setEnabled(true);
            startBtn.setEnabled(false);
            controller.start(this.dir, this.maxFS, this.bands);
        });
        pack(); setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    public void setController(ReportController controller){
        this.controller = controller;
    }

    private void render(String tag) {
        if (this.controller.getReport() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(tag).append("\ntotal=").append(this.controller.getReport().numFiles()).append('\n');
            long max = Math.max(1, this.controller.getReport().numFilesPerBand().stream().mapToLong(Long::longValue).max().orElse(1));
            for (int i = 0; i < bands; i++) {
                long lo = i * maxFS / bands, hi = (i + 1) * maxFS / bands;
                long n  = this.controller.getReport().numFilesPerBand().get(i);
                int  w  = (int) (40.0 * n / max);
                sb.append(String.format("[%10d,%10d) %6d %s%n", lo, hi, n, "#".repeat(w)));
            }
            sb.append(String.format(">= %d           %6d%n", maxFS, this.controller.getReport().numFilesPerBand().get(bands)));
            out.setText(sb.toString());
        }
    }

    public void onComplete(){
        render("Completed");
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public void onStop(){
        this.controller.stop();
        render("Stopped");
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public void onUpdate() {
        render("Update");
    }
}
