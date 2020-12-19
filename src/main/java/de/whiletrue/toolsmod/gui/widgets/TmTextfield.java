package de.whiletrue.toolsmod.gui.widgets;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import de.whiletrue.toolsmod.gui.widgets.preset.TmSizeWidget;
import de.whiletrue.toolsmod.gui.widgets.preset.TmWidget;
import de.whiletrue.toolsmod.util.classes.TextUtil;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.math.MathHelper;

/**
 * This class was free copies from TextfieldWidget to fit the needs of the mod
 * 
 * Made some slide changes
 */

public class TmTextfield extends TmSizeWidget {

	// Text that the field contains
	private String text;
	
	//Text to display if nothing is written
	private String presetString;
	private int presetColor = 0x55AAAA;

	// Max text length of the field
	private int maxStringLength = 32;

	// If the field is enabled
	private boolean enabled = true;

	// Color of the field
	private int enabledColor = 14737632;
	private int disabledColor = 7368816;
	private boolean enableBackgroundDrawing = true;

	// Formatter event handler
	private BiFunction<String, Integer, String> textFormatter = (txt, num) -> txt;
	// Change event handler
	@Nullable
	private Consumer<String> guiResponder;
	// Validition event handler
	private Predicate<String> validator = Predicates.alwaysTrue();

	private int lineScrollOffset, cursorCounter, selectionEnd, cursorPosition;
	private boolean shiftDown;

	public TmTextfield() {
		this(0,0,0,0,"",null);
	}
	
	public TmTextfield(int x, int y, int width, int height, String text,@Nullable Consumer<String> responder) {
		super(x, y, width, height);
		this.text = text;
		this.guiResponder=responder;
	}
	
	public TmTextfield setValidator(Predicate<String> validator) {
		this.validator = validator;
		return this;
	}
	public TmTextfield setResponder(Consumer<String> guiResponder) {
		this.guiResponder = guiResponder;
		return this;
	}
	public TmTextfield setTextFormatter(BiFunction<String, Integer, String> textFormatter) {
		this.textFormatter = textFormatter;
		return this;
	}
	public TmTextfield setText(String text) {
		this.text = text;
		this.lineScrollOffset = this.cursorCounter = this.selectionEnd = this.cursorPosition = 0;
		return this;
	}
	public TmTextfield setMaxStringLength(int maxStringLength) {
		this.maxStringLength = maxStringLength;
		return this;
	}

	@Override
	public void onTick() {
		++this.cursorCounter;
	}

	@Override
	public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
		// Checks if the textfield is enabled
		if (!this.enabled)
			return false;

		this.shiftDown = Screen.hasShiftDown();

		if (Screen.isSelectAll(keyCode)) {
			this.setCursorPosition(this.text.length());
			this.setSelectionPos(0);
			return true;
		} else if (Screen.isCopy(keyCode)) {
			this.mc.keyboardListener.setClipboardString(this.getSelectedText());
			return true;
		} else if (Screen.isPaste(keyCode)) {
			this.writeText(this.mc.keyboardListener.getClipboardString());
			return true;
		}

		switch (keyCode) {
		case 259:
			this.shiftDown = false;
			this.delete(-1);
			this.shiftDown = Screen.hasShiftDown();
			return true;
		case 260:
		case 264:
		case 265:
		case 266:
		case 267:
		default:
			return false;
		case 261:
			this.shiftDown = false;
			this.delete(1);
			this.shiftDown = Screen.hasShiftDown();
			return true;
		case 262:
			if (Screen.hasControlDown())
				this.setCursorPosition(this.getNthWordFromCursor(1));
			else
				this.moveCursorBy(1);
			return true;
		case 263:
			if (Screen.hasControlDown())
				this.setCursorPosition(this.getNthWordFromCursor(-1));
			else
				this.moveCursorBy(-1);
			return true;
		case 268:
			this.setCursorPosition(0);
			return true;
		case 269:
			this.setCursorPosition(this.text.length());
			return true;
		}
	}

	@Override
	public boolean onCharTyped(char key, int keyCode) {
		// Checks if the field is enabled
		if (!this.enabled)
			return false;

		// Checks if the character is allowed to be written
		if (SharedConstants.isAllowedCharacter(key))
			this.writeText(String.valueOf(key));

		return true;
	}

	@Override
	public Optional<TmWidget> onMouseClicked(double mX, double mY, int ticks) {
		// Checks if the field is enabled
		if (!this.enabled || !this.hovered)
			return Optional.empty();

		int i = MathHelper.floor(mX) - this.x;

		if (this.enableBackgroundDrawing)
			i -= 4;

		// Gets the trimmed width
		String trimmed = this.mc.fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset),
				this.getAdjustedWidth());
		// Sets the cursor
		this.setCursorPosition(this.mc.fontRenderer.func_238412_a_(trimmed, i).length() + this.lineScrollOffset);

		return Optional.of(this);
	}

	@Override
	public void onRender(MatrixStack ms,int mX, int mY, float ticks, TmWidget focused) {
		super.onRender(ms,mX, mY, ticks, focused);
		
		// Renders the background
		if (this.enableBackgroundDrawing) {
			AbstractGui.fill(ms,this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
			AbstractGui.fill(ms,this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
		}

		//If the presetString should be used
		boolean usePreset = this.presetString != null && this.text.isEmpty();
		
		//Gets the text to render
		String render = usePreset ? this.presetString : this.text;
		
		int i = !this.enabled ? this.disabledColor : usePreset ? this.presetColor : this.enabledColor;
		int j = this.cursorPosition - this.lineScrollOffset;
		int k = this.selectionEnd - this.lineScrollOffset;
		String s = this.mc.fontRenderer.func_238412_a_(render.substring(this.lineScrollOffset),
				this.getAdjustedWidth());
		boolean flag = j >= 0 && j <= s.length();
		boolean flag1 = this == focused && this.cursorCounter / 6 % 2 == 0 && flag;
		int l = this.enableBackgroundDrawing ? this.x + 4 : this.x;
		int i1 = this.enableBackgroundDrawing ? this.y + (this.height - 8) / 2 : this.y;
		int j1 = l;
		if (k > s.length()) {
			k = s.length();
		}

		if (!s.isEmpty()) {
			String s1 = flag ? s.substring(0, j) : s;
			j1 = this.mc.fontRenderer.drawStringWithShadow(ms,this.textFormatter.apply(s1, this.lineScrollOffset),
					(float) l, (float) i1, i);
		}

		boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.maxStringLength;
		int k1 = j1;
		if (!flag) {
			k1 = j > 0 ? l + this.width : l;
		} else if (flag2) {
			k1 = j1 - 1;
			--j1;
		}

		if (!s.isEmpty() && flag && j < s.length()) {
			this.mc.fontRenderer.drawStringWithShadow(ms,this.textFormatter.apply(s.substring(j), this.cursorPosition),
					(float) j1, (float) i1, i);
		}

		if (flag1) {
			if (flag2) {
				AbstractGui.fill(ms,k1, i1 - 1, k1 + 1, i1 + 1 + 9, -3092272);
			} else {
				this.mc.fontRenderer.drawStringWithShadow(ms,"_", (float) k1, (float) i1, i);
			}
		}

		if (k != j) {
			int l1 = l + this.mc.fontRenderer.getStringWidth(s.substring(0, k));
			this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + 9);
		}

	}

	/**
	 * Sets the current position of the cursor.
	 */
	public void setCursorPosition(int pos) {
		// Re-alignes the cursor
		this.clampCursorPosition(pos);

		// Moves the cursor
		if (!this.shiftDown)
			this.setSelectionPos(this.cursorPosition);

		// Fires the change event
		this.onTextChanged(this.text);
	}

	/**
	 * Ensures that the cursor position is between 0 and the text length
	 * 
	 * @param pos
	 *            the new position of the cursor
	 */
	public void clampCursorPosition(int pos) {
		this.cursorPosition = MathHelper.clamp(pos, 0, this.text.length());
	}

	/**
	 * Fires the text change event
	 * 
	 * @param newText
	 *            the new text
	 */
	private void onTextChanged(String newText) {
		if (this.guiResponder != null)
			this.guiResponder.accept(newText);
	}

	private void delete(int p_212950_1_) {
		// Checks if a word should be deleted
		if (Screen.hasControlDown())
			this.deleteWords(p_212950_1_);
		else
			this.deleteFromCursor(p_212950_1_);
	}

	/**
	 * Deletes the given number of words from the current cursor's position, unless
	 * there is currently a selection, in which case the selection is deleted
	 * instead.
	 */
	public void deleteWords(int num) {
		// Checks if text is set
		if (this.text.isEmpty())
			return;
		
		if (this.selectionEnd != this.cursorPosition)
			this.writeText("");
		else
			this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
	}

	/**
	 * Deletes the given number of characters from the current cursor's position,
	 * unless there is currently a selection, in which case the selection is deleted
	 * instead.
	 */
	public void deleteFromCursor(int num) {
		if (this.text.isEmpty())
			return;

		if (this.selectionEnd != this.cursorPosition)
			this.writeText("");
		else {
			boolean flag = num < 0;
			int i = flag ? this.cursorPosition + num : this.cursorPosition;
			int j = flag ? this.cursorPosition : this.cursorPosition + num;
			String s = "";
			if (i >= 0)
				s = this.text.substring(0, i);

			if (j < this.text.length())
				s = s + this.text.substring(j);

			if (this.validator.test(s)) {
				this.text = s;
				if (flag)
					this.moveCursorBy(num);

				this.onTextChanged(this.text);
			}
		}
	}

	/**
	 * Gets the starting index of the word at the specified number of words away
	 * from the cursor position.
	 */
	public int getNthWordFromCursor(int numWords) {
		return this.getNthWordFromPos(numWords, this.cursorPosition);
	}

	/**
	 * Gets the starting index of the word at a distance of the specified number of
	 * words away from the given position.
	 */
	private int getNthWordFromPos(int n, int pos) {
		return this.getNthWordFromPosWS(n, pos, true);
	}

	/**
	 * Like getNthWordFromPos (which wraps this), but adds option for skipping
	 * consecutive spaces
	 */
	private int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
		int i = pos;
		boolean flag = n < 0;
		int j = Math.abs(n);

		for (int k = 0; k < j; ++k)
			if (!flag) {
				int l = this.text.length();
				i = this.text.indexOf(32, i);
				if (i == -1)
					i = l;
				else
					while (skipWs && i < l && this.text.charAt(i) == ' ')
						++i;
			} else {
				while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ')
					--i;

				while (i > 0 && this.text.charAt(i - 1) != ' ')
					--i;
			}

		return i;
	}

	/**
	 * Moves the text cursor by a specified number of characters and clears the
	 * selection
	 */
	public void moveCursorBy(int num) {
		this.setCursorPosition(this.cursorPosition + num);
	}

	/**
	 * Sets the position of the selection anchor (the selection anchor and the
	 * cursor position mark the edges of the selection). If the anchor is set beyond
	 * the bounds of the current text, it will be put back inside.
	 */
	public void setSelectionPos(int position) {
		int i = this.text.length();
		this.selectionEnd = MathHelper.clamp(position, 0, i);
		if (this.mc.fontRenderer != null) {
			if (this.lineScrollOffset > i)
				this.lineScrollOffset = i;

			int j = this.getAdjustedWidth();
			String s = this.mc.fontRenderer.func_238412_a_(this.text.substring(this.lineScrollOffset), j);
			int k = s.length() + this.lineScrollOffset;
			if (this.selectionEnd == this.lineScrollOffset)
				this.lineScrollOffset -= this.mc.fontRenderer.func_238413_a_(this.text, j, true).length();

			if (this.selectionEnd > k)
				this.lineScrollOffset += this.selectionEnd - k;
			else if (this.selectionEnd <= this.lineScrollOffset)
				this.lineScrollOffset -= this.lineScrollOffset - this.selectionEnd;

			this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
		}
	}

	/**
	 * returns the width of the textbox depending on if background drawing is
	 * enabled
	 */
	public int getAdjustedWidth() {
		return this.enableBackgroundDrawing ? this.width - 8 : this.width;
	}

	/**
	 * returns the text between the cursor and selectionEnd
	 */
	public String getSelectedText() {
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		return this.text.substring(i, j);
	}
	
	public String getText() {
		return this.text;
	}

	/**
	 * Adds the given text after the cursor, or replaces the currently selected text
	 * if there is a selection.
	 */
	public void writeText(String textToWrite) {
		String s = "";
		String s1 = SharedConstants.filterAllowedCharacters(textToWrite);
		int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
		int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
		int k = this.maxStringLength - this.text.length() - (i - j);
		if (!this.text.isEmpty())
			s = s + this.text.substring(0, i);

		int l;
		if (k < s1.length()) {
			s = s + s1.substring(0, k);
			l = k;
		} else {
			s = s + s1;
			l = s1.length();
		}

		if (!this.text.isEmpty() && j < this.text.length())
			s = s + this.text.substring(j);

		if (!this.validator.test(s))
			return;

		this.text = s;
		this.clampCursorPosition(i + l);
		this.setSelectionPos(this.cursorPosition);
		this.onTextChanged(this.text);
	}

	/**
	 * Draws the blue selection box.
	 */
	private void drawSelectionBox(int startX, int startY, int endX, int endY) {
		if (startX < endX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		if (startY < endY) {
			int j = startY;
			startY = endY;
			endY = j;
		}

		if (endX > this.x + this.width)
			endX = this.x + this.width;

		if (startX > this.x + this.width)
			startX = this.x + this.width;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos((double) startX, (double) endY, 0.0D).endVertex();
		bufferbuilder.pos((double) endX, (double) endY, 0.0D).endVertex();
		bufferbuilder.pos((double) endX, (double) startY, 0.0D).endVertex();
		bufferbuilder.pos((double) startX, (double) startY, 0.0D).endVertex();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
	
	
	public TmTextfield setPresetStringByKey(String key,Object...args) {
		this.presetString = TextUtil.getInstance().getByKey(key,args);
		return this;
	}
	
	public TmTextfield setPresetColor(int presetColor) {
		this.presetColor = presetColor;
		return this;
	}
	public TmTextfield setPresetString(String presetString) {
		this.presetString = presetString;
		return this;
	}
}
