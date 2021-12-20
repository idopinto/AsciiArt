package ascii_art.img_to_char;

import image.Image;

import java.awt.*;
import java.util.HashMap;

public class BrightnessImgCharMatcher {

    /* Instance fields */
    private final Image image;
    private final String fontName;
    private final HashMap<Image, Float> cache = new HashMap<>();

    /**
     *
     * Constructor.
     * @param image image to convert
     * @param fontName font name
     */
    public BrightnessImgCharMatcher(Image image, String fontName)
    {
        this.image = image;
        this.fontName = fontName;
    }

    /**
     *  chooseChars
     * @param numCharsInRow number of characters each row in ASCII image.
     * @param charSet array of possible ASCII image characters.
     * @return 2-d array of chars which represents image
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet)
    {
        // TODO charSet.length == 0 return new char[][]
        if (charSet.length == 0){
            return new char[0][];
        }
        return convertImageToAscii(numCharsInRow,linearStretching(getBrightnessArray(charSet)),charSet);
    }


    /*
        this method gets character set and return the array that holds the brightness level
         of each character (according to font)
     */
    private float[] getBrightnessArray(Character[] charSet)
    {
        int sum;
        float[] brightnessArray = new float[charSet.length];
        for (int i = 0; i < charSet.length;++i) {

            boolean[][] matrix = CharRenderer.getImg(charSet[i],16, fontName);
            sum = 0;
            for (boolean[] row : matrix) {
                for (boolean b : row) {
                    if (b) {++sum;}
                }
            }
            brightnessArray[i] = sum / 256f;
        }
        return brightnessArray;
    }

    /*
        this method gets brightness Array and apply linear stretching on each value.
        and return the new array after stretching.
     */
    private float[] linearStretching(float[] brightnessArray)
    {
        float[] minMax = findMaxMinValue(brightnessArray);
        for (int i = 0; i < brightnessArray.length; i++) {
            brightnessArray[i] = normalizeCharBrightness(brightnessArray[i], minMax);
        }
        return brightnessArray;
    }

    /*
        this method gets array that hold the max and min brightness.
        and get the char brightness value.
        calculates the normalized char brightness value according to
        formula and return the result.
     */
    private float normalizeCharBrightness(float b,float[] minMax)
    {
        if (minMax[1] == minMax[0])
        {
            return 0;
        }
        return (b-minMax[0])/(minMax[1]-minMax[0]);
    }


    /*
        this method gets brightnessArray, iterates over the array and returns array
        which holds the maximum and minimum brightness value.
     */
    private float[] findMaxMinValue(float[] brightnessArray)
    {
        float[] minMax = {brightnessArray[0],brightnessArray[0]}; // min -> 0.89453125, max -> 0.89453125
        for (float b: brightnessArray) {
            if (b < minMax[0])
            {
                minMax[0] = b;
            }
            if (b > minMax[1]) {
                minMax[1] = b;
            }
        }
        return minMax;
    }

    /*
        this method gets image. and returns the average brightness level.
     */
    private float findAverageBrightnessLevel(Image image)
    {
        float sum = 0;
        for (Color pixel: image.pixels()){
            // write you code here
            sum += convertToGreyPixel(pixel);
        }
        return sum / (image.getWidth()*image.getHeight()) / 255f;
    }

    /*
        this method gets pixel and convert it to grey pixel according to this formula.
     */
    private float convertToGreyPixel(Color color)
    {
        return (float) (color.getRed() * 0.2126 + color.getGreen() * 0.7152 + color.getBlue() * 0.0722);
    }

    /*
        this method apply conversion from image  to its ascii representation.
        this method divides the image to sub-images, iterates over the sub-images
        and for each sub-image it determines which ascii character will replace the sub-image
        in the final result.
     */
    private char[][] convertImageToAscii(int numCharsInRow,float[] brightnessArray, Character[] charSet)
    {
        int pixels = image.getWidth() / numCharsInRow;
        float brightnessLevel;
        int row = 0 , col = 0, z;
        char[][] asciiArt = new char[image.getHeight()/pixels][image.getWidth()/pixels];
        for(Image subImage : image.squareSubImagesOfSize(pixels)) {
            brightnessLevel =(cache.containsKey(subImage)) ? cache.get(subImage) : findAverageBrightnessLevel(subImage);
            cache.put(subImage,brightnessLevel);
            z = closetBrightnessLevelIndex(brightnessArray,brightnessLevel);
            asciiArt[row][col] = charSet[z];

            if (col >= asciiArt[0].length - 1)
            {
                col = 0;
                row++;
            }
            else {
                col++;
            }

        }
        return asciiArt;
    }

    /*
        this method gets brightnessArray and float number.
        iterates over the brightness array and return the index of
        the minimum difference in absolute value.
        ( min( |v[i] - x| ) for every 0 <= i < n
        v: brightnessArray , n = brightnessArray.length, x= valueToCompare
     */
    private int closetBrightnessLevelIndex(float[] brightnessArray, float valueToCompare) {
        int minIndex = 0;
        float minDifference = Math.abs(brightnessArray[0]-valueToCompare);
        for (int i = 0; i < brightnessArray.length; i++) {
            if (Math.abs(brightnessArray[i]-valueToCompare) < minDifference)
            {
                minDifference = Math.abs(brightnessArray[i]-valueToCompare);
                minIndex = i;
            }
        }
        return minIndex;
    }


}
