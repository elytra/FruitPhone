/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Una Thompson (unascribed)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package mcp.mobius.waila.api;

import java.util.HashMap;
import java.util.Set;

/**
 * Read-only interface for Waila internal config storage.<br>
 * An instance of this interface is passed to most of Waila callbacks as a way to change the behavior depending on client settings.
 * 
 * @author ProfMobius
 *
 */
public interface IWailaConfigHandler {
	/** Returns a set of all the currently loaded modules in the config handler.
	 * 
	 * @return
	 */
	public Set<String> getModuleNames();
	
	/**
	 * Returns all the currently available options for a given module
	 * 
	 * @param modName Module name
	 * @return
	 */
	public HashMap<String, String> getConfigKeys(String modName);
	
	/**
	 * Returns the current value of an option (true/false) with a default value if not set.
	 * 
	 * @param key Option to lookup
	 * @param defvalue Default values
	 * @return Value of the option or defvalue if not set.
	 */
	public boolean getConfig(String key, boolean defvalue);
	
	/**
	 * Returns the current value of an option (true/false) with a default value true if not set
	 * 
	 * @param key Option to lookup
	 * @return Value of the option or true if not set.
	 */
	public boolean getConfig(String key);	
}
