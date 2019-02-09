package thito.breadcore.spigot.objects;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

public class SpigotGameProfile extends GameProfile {

	public static void main(String[]args) throws Exception {
		String ss;
		System.out.println(ss=new String(Base64.getDecoder().decode(
				"eyJ0aW1lc3RhbXAiOjE1MjYzMDUxMDU2NTAsInByb2ZpbGVJZCI6IjBiZTk0Njg3OTE0ZjRiMDFiYmFiY2Q5MjU0NDA5ZjM4IiwicHJvZmlsZU5hbWUiOiJUZXMiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZmODQxMmNkNzU0YjUxYzc0MDEwZjUxZmI2NDNlY2Q3NTM1ZjVjMzlkZjk4NTFlMTc5YjZmZTFlMjk5NmNmIn19fQ=="
				)));
		FileOutputStream str = new FileOutputStream("C:/sb.png");
		str.write(ss.getBytes());
		str.close();
	}
	public SpigotGameProfile(UUID uUID, String string, InputStream skin) {
		super(uUID, string);
	}

}
