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
 

package de.dakror.arise.server.data;

import de.dakror.arise.settings.Resources;
import de.dakror.arise.settings.TransferType;

/**
 * @author Dakror
 */
public class TransferData {
	public int id, timeleft, cityFromId, cityToId;
	public Resources value;
	public TransferType type;
	
	public TransferData(int id, int timeleft, int cityFromId, int cityToId, Resources value, TransferType type) {
		this.id = id;
		this.timeleft = timeleft;
		this.cityFromId = cityFromId;
		this.cityToId = cityToId;
		this.value = value;
		this.type = type;
	}
}
