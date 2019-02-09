package thito.breadcore.utils;

import org.bukkit.ChatColor;

/**
 *
 * Created by SirSpoodles Modified by ZombieHDGaming for MotD Usage
 *
 * https://www.spigotmc.org/threads/free-code-sending-perfectly-centered-chat-message.95872/
 *
 * Contains original method, method MK.2, and MotD method
 *
 */
public enum Chat {
	A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5), F('F',
			5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j', 5), K(
					'K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o(
							'o', 5), P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s',
									5), T('T', 5), t('t', 4), U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W',
											5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5), Z('Z', 5), z('z',
													5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4',
															5), NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8',
																	5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT(
																			'!', 1), AT_SYMBOL('@', 6), NUM_SIGN('#',
																					5), DOLLAR_SIGN('$', 5), PERCENT(
																							'%', 5), UP_ARROW('^',
																									5), AMPERSAND('&',
																											5), ASTERISK(
																													'*',
																													5), LEFT_PARENTHESIS(
																															'(',
																															4), RIGHT_PERENTHESIS(
																																	')',
																																	4), MINUS(
																																			'-',
																																			5), UNDERSCORE(
																																					'_',
																																					5), PLUS_SIGN(
																																							'+',
																																							5), EQUALS_SIGN(
																																									'=',
																																									5), LEFT_CURL_BRACE(
																																											'{',
																																											4), RIGHT_CURL_BRACE(
																																													'}',
																																													4), LEFT_BRACKET(
																																															'[',
																																															3), RIGHT_BRACKET(
																																																	']',
																																																	3), COLON(
																																																			':',
																																																			1), SEMI_COLON(
																																																					';',
																																																					1), DOUBLE_QUOTE(
																																																							'"',
																																																							3), SINGLE_QUOTE(
																																																									'\'',
																																																									1), LEFT_ARROW(
																																																											'<',
																																																											4), RIGHT_ARROW(
																																																													'>',
																																																													4), QUESTION_MARK(
																																																															'?',
																																																															5), SLASH(
																																																																	'/',
																																																																	5), BACK_SLASH(
																																																																			'\\',
																																																																			5), LINE(
																																																																					'|',
																																																																					1), TILDE(
																																																																							'~',
																																																																							5), TICK(
																																																																									'`',
																																																																									2), PERIOD(
																																																																											'.',
																																																																											1), COMMA(
																																																																													',',
																																																																													1), SPACE(
																																																																															' ',
																																																																															3), DEFAULT(
																																																																																	'a',
																																																																																	4);

	private char character;
	private int length;

	private final static int CENTER_PX = 127;
	private final static int MAX_PX = 240;

	private final static int CENTER_CHAT_PX = 154;
	private final static int MAX_CHAT_PX = 250;

	Chat(char character, int length) {
		this.character = character;
		this.length = length;
	}

	public char getCharacter() {
		return character;
	}

	public int getLength() {
		return length;
	}

	public int getBoldLength() {
		if (this == Chat.SPACE) {
			return getLength();
		}
		return length + 1;
	}

	public static Chat getDefaultFontInfo(char c) {
		for (final Chat dFI : Chat.values()) {
			if (dFI.getCharacter() == c) {
				return dFI;
			}
		}
		return Chat.DEFAULT;
	}

	public static String centerMotD(String message) {
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		int charIndex = 0;
		int lastSpaceIndex = 0;
		String toSendAfter = null;
		String recentColorCode = "";
		for (final char c : message.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode == true) {
				previousCode = false;
				recentColorCode = "§" + c;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else if (c == ' ') {
				lastSpaceIndex = charIndex;
			} else {
				final Chat dFI = Chat.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
			if (messagePxSize >= MAX_PX) {
				toSendAfter = recentColorCode + message.substring(lastSpaceIndex + 1, message.length());
				message = message.substring(0, lastSpaceIndex + 1);
				break;
			}
			charIndex++;
		}
		final int halvedMessageSize = messagePxSize / 2;
		final int toCompensate = CENTER_PX - halvedMessageSize;
		final int spaceLength = Chat.SPACE.getLength() + 1;
		int compensated = 0;
		final StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		if (toSendAfter != null) {
			centerMotD(toSendAfter);
		}
		return sb.toString() + message;
	}

	public static String line(String s) {
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (final char c : s.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else {
				final Chat dFI = Chat.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}
		String builder = new String();
		for (int i = 0; i < MAX_CHAT_PX; i += messagePxSize) {
			builder += s;
		}
		return builder;
	}

	public static String center(String s) {
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (final char c : s.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else {
				final Chat dFI = Chat.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		final int halvedMessageSize = messagePxSize / 2;
		final int toCompensate = CENTER_PX - halvedMessageSize;
		final int spaceLength = Chat.SPACE.getLength() + 1;
		int compensated = 0;
		final StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + s;
	}
	
	public static String center(String s,int pixelLength) {
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for (final char c : s.toCharArray()) {
			if (c == '§') {
				previousCode = true;
				continue;
			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
					continue;
				} else {
					isBold = false;
				}
			} else {
				final Chat dFI = Chat.getDefaultFontInfo(c);
				messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
				messagePxSize++;
			}
		}

		final int halvedMessageSize = messagePxSize / 2;
		final int toCompensate = pixelLength - halvedMessageSize;
		final int spaceLength = Chat.SPACE.getLength() + 1;
		int compensated = 0;
		final StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + s;
	}

	public static void main(String[]args) {
		for (String s : centerMultilines("abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopqrstuvwxyz")) {
			System.out.println(s);
		}
	}
	public static String[] centerMultilines(String message) {
		message = ChatColor.translateAlternateColorCodes('&', message);
		String builder = new String();
		String later = new String();
		int pixels = 0;
		boolean wasColor = false;
		boolean bold = false;
		for (char c : message.toCharArray()) {
			if (c == '§') {
				wasColor = true;
			} else if (wasColor) {
				wasColor = false;
				bold = c == 'l' || c == 'L';
				builder+='§'+String.valueOf(c);
			} else {
				Chat size = getDefaultFontInfo(c);
				pixels += (bold ? size.getBoldLength() : size.getLength()) + 1;
				if (pixels >= MAX_CHAT_PX) {
					later+=c;
				} else {
					builder+=c;
				}
			}
		}
		String spaces = new String();
		int spacesLength = 0;
		while (spacesLength < (CENTER_CHAT_PX / 2 - (pixels / 2))) {
			spaces+=' ';
			spacesLength+=SPACE.getLength() + 1;
		}
		if (!later.isEmpty()) {
			return ArrayUtil.combine(new String[]{builder}, centerMultilines(later));
		}
		return new String[] {spaces + builder};
	}

}