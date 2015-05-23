/*******************************************************************************
 * Copyright 2015 Maximilian Stark | Dakror <mail@dakror.de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
 

package de.dakror.arise.net.packet;



/**
 * @author Dakror
 */
public class Packet01Login extends Packet {
	public static enum Response {
		LOGIN_OK(""),
		BAD_LOGIN("Login inkorrekt!"),
		ALREADY_LOGGED_IN("Dein Account ist schon von einem anderen Gerät aus angemeldet."),
		BAD_WORLD_ID("Ungültige Welt-ID!");
		
		public String text;
		
		private Response(String text) {
			this.text = text;
		}
	}
	
	String username;
	String pwdMd5;
	Response reponse;
	int userId;
	int worldId;
	
	public Packet01Login(String username, String pwdMd5, int worldId) {
		super(1);
		this.username = username;
		this.pwdMd5 = pwdMd5;
		this.worldId = worldId;
	}
	
	public Packet01Login(String username, int userId, int worldId, Response reponse) {
		super(1);
		this.username = username;
		this.reponse = reponse;
		this.userId = userId;
		this.worldId = worldId;
	}
	
	public Packet01Login(byte[] data) {
		super(1);
		
		String[] parts = readData(data).split(":");
		username = new String(new String(parts[0]));
		if (parts.length == 4) {
			userId = Integer.parseInt(new String(parts[1]));
			reponse = Response.values()[Integer.parseInt(new String(parts[2]))];
			worldId = Integer.parseInt(new String(parts[3]));
		} else if (parts.length == 3) {
			pwdMd5 = new String(new String(parts[1]));
			worldId = Integer.parseInt(new String(parts[2]));
		} else throw new IllegalArgumentException("To few parameters for packet!");
	}
	
	@Override
	protected byte[] getPacketData() {
		if (pwdMd5 == null) return (username + ":" + userId + ":" + reponse.ordinal() + ":" + worldId).getBytes();
		else return (username + ":" + pwdMd5 + ":" + worldId).getBytes();
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPwdMd5() {
		return pwdMd5;
	}
	
	public Response getResponse() {
		return reponse;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public int getWorldId() {
		return worldId;
	}
}
