/**
 *  Copyright (C) 2017 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright (C) 2017 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.util;

import brut.common.BrutException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AaptManager {

    public static File getAppt2() throws BrutException {
        return getAppt(2);
    }

    public static File getAppt1() throws BrutException {
        return getAppt(1);
    }

    private static File getAppt(Integer version) throws BrutException {
        File aaptBinary;
        String aaptVersion = "aapt" + (version == 2 ? "2" : null);

        if (! OSDetection.is64Bit() && ! OSDetection.isWindows()) {
            throw new BrutException("32 bit OS detected. No 32 bit binaries available.");
        }

        try {
            if (OSDetection.isMacOSX()) {
                aaptBinary = Jar.getResourceAsFile("/prebuilt/" + aaptVersion + "/macosx/" + aaptVersion, AaptManager.class);
            } else if (OSDetection.isUnix()) {
                aaptBinary = Jar.getResourceAsFile("/prebuilt/" + aaptVersion + "/linux/" + aaptVersion, AaptManager.class);
            } else if (OSDetection.isWindows()) {
                aaptBinary = Jar.getResourceAsFile("/prebuilt/" + aaptVersion + "/windows/" + aaptVersion + ".exe", AaptManager.class);
            } else {
                throw new BrutException("Could not identify platform: " + OSDetection.returnOS());
            }
        } catch (BrutException ex) {
            throw new BrutException(ex);
        }

        if (aaptBinary.setExecutable(true)) {
            return aaptBinary;
        }

        throw new BrutException("Can't set aapt binary as executable");
    }

    public static int getAaptVersion(String aaptLocation) throws BrutException {
        return getApptVersion(new File(aaptLocation));
    }

    public static int getApptVersion(File aapt) throws BrutException {
        if (!aapt.isFile()) {
            throw new BrutException("Could not identify aapt binary as executable.");
        }
        aapt.setExecutable(true);

        List<String> cmd = new ArrayList<>();
        cmd.add(aapt.getAbsolutePath());
        cmd.add("version");

        String version = OS.execAndReturn(cmd.toArray(new String[0]));

        if (version == null) {
            throw new BrutException("Could not execute aapt binary at location: " + aapt.getAbsolutePath());
        }

        if (version.startsWith("Android Asset Packaging Tool (aapt) 2:")) {
            return 2;
        } else if (version.startsWith("Android Asset Packaging Tool, v0.")) {
            return 1;
        }

        throw new BrutException("aapt version could not be identified: " + version);
    }
}
