package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.AsciiOutput;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

// Regex
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// IO
import java.util.Scanner;

//  Data structures imports
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 * This class represents user interface which assist to convert images to ASCII art easily
 */
public class Shell {

    /* Error messages constants */
    private static final String ERR_MSG = "Invalid command";
    private static final String MAX_RES_MSG = "You're using the maximum resolution";
    private static final String MIN_RES_MSG = "You're using the minimum resolution";

    /* Shell commands constants */
    private static final String CMD_EXIT = "exit";
    private static final String CMD_CONSOLE = "console";
    private static final String CMD_ADD = "add";
    private static final String CMD_CHARS = "chars";
    private static final String CMD_REMOVE = "remove";
    private static final String CMD_RENDER = "render";
    private static final String CMD_RES = "res";
    private static final int RES_GROWTH_FACTOR = 2;

    /* initialization constants */
    private static final String FONT_NAME = "Courier New";
    private static final String OUTPUT_FILENAME = "out.html";
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final String INITIAL_CHARS_RANGE = "0-9";

    /* class variables */
    private Set<Character> charSet = new HashSet<>();
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private int charsInRow;
    private final BrightnessImgCharMatcher charMatcher;
    private AsciiOutput output;

    /**
     * constructor
     * @param img The image to convert to ascii.
     */
    public Shell(Image img) {
        // init min and max chars in row.
        this.minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        this.maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
        charMatcher = new BrightnessImgCharMatcher(img, FONT_NAME);
        output = new HtmlAsciiOutput(OUTPUT_FILENAME, FONT_NAME);
        changeCharSet(INITIAL_CHARS_RANGE,charSet::add);
    }



    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.print(">>> ");
        String cmd = scanner.nextLine().trim();
        String[] words = cmd.split("\\s+"); // splits according to whitespaces

        while (!words[0].equals(CMD_EXIT)) {

            // handles empty input
            if (!words[0].equals("")) {
                String param = "";
                if (words.length > 1) {
                    param = words[1];
                }
            }

            // do stuff according to given command
            doCommand(words);
            System.out.print(">>> ");
            cmd = scanner.nextLine().trim();
            words = cmd.split("\\s+");
        }
    }

    /* this method displays the char set */
    private void showChars() {
        this.charSet.stream().sorted().forEach(c-> System.out.print(c + " "));
        System.out.println();
    }



    /* this method get add\remove methods and range(string), and add\remove the chars in the range */
    private void changeCharSet(String s, Consumer<Character> consumer)
    {
        char[] range = parseCharRange(s);
        if(range != null){
            Stream.iterate(range[0], c -> c <= range[1], c -> (char)((int)c+1)).forEach(consumer);
            return;
        }
        System.out.println(ERR_MSG);

    }

    /*this function parses the given string in an add or remove request*/
    private static char[] parseCharRange(String param)
    {

        if (param.equals("all")) {
            return new char[]{' ','~'};
        }
        if (param.equals("space")) {
            return new char[]{' ',' '};
        }
        Pattern pattern = Pattern.compile("^[\\w|\\D{1}]$|^[\\w|\\D{1}]-[\\w|\\D{1}]$");
        Matcher matcher = pattern.matcher(param);

        if(matcher.matches()) {
            char ch1 = param.charAt(matcher.start());
            char ch2 = param.charAt(matcher.end()-1);
            return (ch1 > ch2) ? new char[]{ ch2,ch1} : new char[]{ ch1,ch2};
        }
        return null;
    }

    /*this function change the resolution of the output image*/
    private void resChange(String s)
    {
        if (s.equals("up"))
        {
            if (charsInRow > maxCharsInRow)
            {
                System.out.println(MAX_RES_MSG);
                return;
            }
            charsInRow = charsInRow * RES_GROWTH_FACTOR;
            System.out.print("Width set to " + charsInRow + "\n");
            }
        else if (s.equals("down"))
        {
            if (charsInRow < minCharsInRow)
            {
                System.out.println(MIN_RES_MSG);
                return;
            }
            charsInRow = charsInRow / RES_GROWTH_FACTOR;
            System.out.print("Width set to " +charsInRow + "\n");
        }
        else{
            System.out.println(ERR_MSG);
        }
    }

    /*this method render the image */
    private void render(){
        if (charSet.size() != 0 )
        {
            Character[] charSet1 = new Character[charSet.size()];
            charSet.toArray(charSet1);
            char[][] chars = charMatcher.chooseChars(charsInRow, charSet1);
            output.output(chars);
        }
    }

    /*
       this method sets the output to Console output so next time the user writes 'render'
       the output will be on the console.
     */
    private void console() {
        this.output = new ConsoleAsciiOutput();
    }

    /*this method gets cmd arguments array and call the desired method according to input */
    private void doCommand(String[] words) {

        String cmd = words[0];
        if (words.length == 1) {
            switch (cmd) {
                case CMD_CHARS:
                    showChars();
                    break;
                case CMD_CONSOLE:
                    console();
                    break;
                case CMD_RENDER:
                    render();
                    break;
                case "":
                    break;
                default:
                    System.out.println(ERR_MSG);
            }
        } else if (words.length == 2) {
            switch (cmd) {
                case CMD_ADD:
                    changeCharSet(words[1], charSet::add);
                    break;
                case CMD_REMOVE:
                    changeCharSet(words[1], charSet::remove);
                    break;
                case CMD_RES:
                    resChange(words[1]);
                    break;
                default:
                    System.out.println(ERR_MSG);
            }
        } else {
            System.out.println(ERR_MSG);
        }
    }


}