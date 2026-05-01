import javax.swing.*;
import java.awt.*;

public class ReportView extends JFrame {
    private final JButton stopBtn  = new JButton("Stop");
    private final JTextArea out    = new JTextArea(20, 50);
    private final InteractiveVirtualThreadsReport reportCalculator;

    private final long maxFS;
    private final int bands;

    public ReportView(InteractiveVirtualThreadsReport reportCalculator, long maxFS, int bands) {
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
        stopBtn.setEnabled(true);
        out.setEditable(false); out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        out.setText("");

        this.maxFS = maxFS;
        this.bands = bands;
        this.reportCalculator = reportCalculator;

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(out), BorderLayout.CENTER);

        stopBtn.addActionListener(_ -> onStop());

        pack(); setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    private void render(String tag) {
        StringBuilder sb = new StringBuilder();
        sb.append(tag).append("total=").append(reportCalculator.getReport().numFiles()).append('\n');
        long max = Math.max(1, reportCalculator.getReport().numFilesPerBand().stream().mapToLong(Long::longValue).max().orElse(1));
        for (int i = 0; i < bands; i++) {
            long lo = i * maxFS / bands, hi = (i + 1) * maxFS / bands;
            long n  = reportCalculator.getReport().numFilesPerBand().get(i);
            int  w  = (int) (40.0 * n / max);
            sb.append(String.format("[%10d,%10d) %6d %s%n", lo, hi, n, "#".repeat(w)));
        }
        sb.append(String.format(">= %d           %6d%n", maxFS, reportCalculator.getReport().numFilesPerBand().get(bands)));
        out.setText(sb.toString());
    }

    public void onComplete(){
        render("Completed");
        stopBtn.setEnabled(false);
    }

    public void onStop(){
        reportCalculator.stop();
        render("Stopped");
        stopBtn.setEnabled(false);
    }

    public void onUpdate() {
        render("Update");
    }
}
