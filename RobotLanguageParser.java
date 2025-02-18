import java.io.*;
import java.util.*;
import java.util.regex.*;

public class RobotLanguageParser {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\|([a-z][a-z0-9]*(?:[\\s,]+[a-z][a-z0-9]*)*)\\|");
    private static final Pattern PROCEDURE_PATTERN = Pattern.compile("proc\\s+[a-z][a-zA-Z0-9]*:*(\\s+[a-z][a-zA-Z0-9]*)*(\\s+and[a-zA-Z0-9]*:\\s+[a-z][a-zA-Z0-9]*)*\\s+\\[");
    private static final Pattern COMMAND_PATTERN = Pattern.compile("((nop\\s*.\\s*|goto\\s*:\\s*([0-9]+|[a-z][a-z0-9]*))|(turn\\s*:\\s*(#left|#right|#around))|(face\\s*:\\s*(#north|#south|#west|#east))|((put|pick)\\s*:\\s*([a-z][a-z0-9]*)(\\s*ofType:\\s*(#balloons|#chips))?\\s*.\\s*)|((jump|move)\\s*:\\s*[a-z][a-z0-9]*(\\s*(inDir:\\s*(#north|#south|#west|#east)|toThe:\\s*(#front|#right|#left|#back)))))*");
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("([a-z][a-z0-9]*)\\s*:=\\s*([a-z0-9#]+)\\s*\\.");
    private static final Pattern CONDITIONAL_PATTERN = Pattern.compile(
    "(if|while)\\s*:\\s*" +  
    "(facing:\\s*(#north|#south|#west|#east)|" +  
    "(canPut|canPick)\\s*:\\s*([a-z][a-z0-9]*|\\d+)\\s*ofType:\\s*(#balloons|#chips)|" +  
    "(canMove|canJump)\\s*:\\s*([a-z0-9]*|\\d+)\\s*(inDir:\\s*(#north|#south|#west|#east)|toThe:\\s*(#front|#right|#left|#back))|" +  
    "not:\\s*\\w+)" +  
    "\\s*(then:\\s*\\[(\\s\\?)\\]\\s*else:\\s*\\[([\\s\\S]*?)\\]|do:\\s*\\[([\\s\\S]*?)\\])");

    public boolean parseFile(String filePath) {
        boolean isValid = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                System.out.println(line);
                if (!line.isEmpty()) {
                    if (!validateLine(line)) {
                        isValid = false;
                        break;  
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return isValid;
    }

    private boolean validateLine(String line) {
        // Validar declaración de variables
        if (line.startsWith("|")) {
            Matcher matcher = VARIABLE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                error("Invalid variable declaration: " + line);
                return false;
            }
        }
        // Validar declaración de procedimientos
        else if (line.startsWith("proc")) {
            Matcher matcher = PROCEDURE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                error("Invalid procedure declaration: " + line);
                return false;
            }
        }
        // Validar comandos
        else if (line.startsWith("move") || line.startsWith("turn") || line.startsWith("face") || line.startsWith("put") || line.startsWith("pick") || line.startsWith("jump") || line.startsWith("goto")) {
            Matcher matcher = COMMAND_PATTERN.matcher(line);
            if (!matcher.matches()) {
                error("Invalid command: " + line);
                return false;
            }
        }
        else if (line.startsWith("if") || line.startsWith("while")) {
            Matcher matcher = CONDITIONAL_PATTERN.matcher(line);
            if (!matcher.matches()) {
                error("Invalid conditional: " + line);
                return false;
            }
        }
        else if (line.startsWith("[") || line.startsWith("]")) {
            return true;
        }
        else if (line.contains(":=")){
            Matcher matcher = ASSIGNMENT_PATTERN.matcher(line);
            if (!matcher.matches()) {
                error("Invalid assignment: " + line);
                return false;
            }
        }
        return true;
    }

    private void error(String message) {
        System.out.println("Syntax error: " + message);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Ingrese el nombre del archivo TXT: ");
        String filePath = scanner.nextLine();
        scanner.close();

        RobotLanguageParser parser = new RobotLanguageParser();
        boolean isValid = parser.parseFile(filePath);
        System.out.println(isValid ? "Sí" : "No");
    }
}
