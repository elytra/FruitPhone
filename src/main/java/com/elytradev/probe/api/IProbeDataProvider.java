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

package com.elytradev.probe.api;

import java.util.List;

/**
 * Capability representing an object's ability to provide detailed information to some kind of probe or UI element. The
 * simple case is something like WAILA, HWYLA, or TheOneProbe. However, this API could be used by snap-on monitor
 * blocks, remote monitoring systems, or data-based automation. Picture this: "activate redstone when bar labeled
 * 'temperature' is greater than 80%". Structured data is very useful data.
 * 
 * <p>This interface addresses how you get the data. What you do with it is up to you.
 * 
 * <p>Probes and other devices should gather data only on the server Side. Implementors are encouraged to ignore
 * clientside probe requests.
 */
public interface IProbeDataProvider {
	public void provideProbeData(List<IProbeData> data);
}
