public class VirtualThreadsExample {
    public static void main(){

        InteractiveVirtualThreadsReport model = new InteractiveVirtualThreadsReport();
        ReportView view = new ReportView();
        ReportController controller = new ReportController(view, model);
        view.setController(controller);
        model.setController(controller);

        view.setVisible(true);
    }
}
