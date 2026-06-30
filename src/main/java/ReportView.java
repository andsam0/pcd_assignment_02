import javax.swing.*;
import java.awt.*;

public class ReportView extends JFrame {
    private final JButton stopBtn = new JButton("Stop");
    private final JButton startBtn = new JButton("Start");
    private final JTextArea out = new JTextArea(20, 50);
    private final InteractiveVirtualThreadsReport reportCalculator;
    private final JTextField dirField;
    private final JTextField maxFsField;
    private final JTextField bandsField;

    private String dir;
    private long maxFS;
    private int bands;

    public ReportView(InteractiveVirtualThreadsReport reportCalculator, long maxFS, int bands, String directory) {
        super("FSStat — Virtual Threads");
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        top.add(new JLabel("Dir:"));
        dirField = new JTextField(directory, 25);
        top.add(dirField);
        top.add(new JLabel("MaxFS:"));
        maxFsField = new JTextField(String.valueOf(maxFS), 10);
        top.add(maxFsField);
        top.add(new JLabel("Bands:"));
        bandsField = new JTextField(String.valueOf(bands), 4);
        top.add(bandsField);
        top.add(stopBtn);
        top.add(startBtn);
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);
        out.setEditable(false);
        out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        out.setText("");

        this.maxFS = maxFS;
        this.bands = bands;
        this.dir = directory;
        this.reportCalculator = reportCalculator;

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(out), BorderLayout.CENTER);

        stopBtn.addActionListener(_ -> onStop());
        startBtn.addActionListener(_ -> onStart());

        pack();
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void render(String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag).append(" total=").append(reportCalculator.getReport().numFiles()).append('\n');
        long max = Math.max(1, reportCalculator.getReport().numFilesPerBand().stream().mapToLong(Long::longValue).max().orElse(1));
        for (int i = 0; i < bands; i++) {
            long lo = i * maxFS / bands, hi = (i + 1) * maxFS / bands;
            long n = reportCalculator.getReport().numFilesPerBand().get(i);
            int w = (int) (40.0 * n / max);
            sb.append(String.format("[%10d,%10d) %6d %s%n", lo, hi, n, "#".repeat(w)));
        }
        sb.append(String.format(">= %d           %6d%n", maxFS, reportCalculator.getReport().numFilesPerBand().get(bands)));
        out.setText(sb.toString());
    }

    public void onComplete() {
        render("Completed");
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public void onStop() {
        reportCalculator.stop();
        render("Stopped");
        stopBtn.setEnabled(false);
        startBtn.setEnabled(true);
    }

    public void onStart() {
        maxFS = Long.parseLong(maxFsField.getText());
        bands = Integer.parseInt(bandsField.getText());
        dir = dirField.getText();
        reportCalculator.start(dir, maxFS, bands);
        render("Started");
        stopBtn.setEnabled(true);
        startBtn.setEnabled(false);

    }

    public void onUpdate() {
        render("Update");
    }
}
