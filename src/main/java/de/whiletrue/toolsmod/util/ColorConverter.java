package de.whiletrue.toolsmod.util;
import java.awt.Color;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.util.Arrays;

/**
 *	Used from: https://stackoverflow.com/a/43704882
 */

public class ColorConverter {
    private final Color[] colors;
    private final IndexColorModel colorModel;

    public ColorConverter(int... colors) {
    	this(Arrays.stream(colors).mapToObj(i->new Color(i|0xff000000)).toArray(Color[]::new));
    }
    
    public ColorConverter(Color... colors) {
        this.colors = colors;
        this.colorModel = createColorModel(colors);
    }

    private static IndexColorModel createColorModel(Color[] colors) {
        final int[] cmap = new int[colors.length];
        for (int i = 0; i<colors.length; i++) {
            cmap[i] = colors[i].getRGB();
        }
        final int bits = (int) Math.ceil(Math.log(cmap.length)/Math.log(2));
        return new IndexColorModel(bits, cmap.length, cmap, 0, false, -1, DataBuffer.TYPE_BYTE);
    }
    
    public int nearestIndex(Color color) {
    	return ((byte[])colorModel.getDataElements(color.getRGB(), null))[0];
    }

    public Color nearestColor(Color color) {
        return colors[this.nearestIndex(color)];
    }
}