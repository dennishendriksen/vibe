package org.molgenis.vibe.core;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum TestData {
    HPO_OWL {
        @Override
        public String getName() {
            return "shared/hp.owl";
        }
    },
    FAKE_HPO_OWL {
        @Override
        public String getName() {
            return "shared/fake_hp.owl";
        }
    },
    TTL {
        @Override
        public String getName() {
            return "shared/vibe.ttl";
        }
    },
    TDB {
        @Override
        public String getName() {
            return "shared/tdb";
        }
    },
    HDT {
        @Override
        public String getName() {
            return "shared/hdt/vibe.hdt";
        }
    },
    EXISTING_TSV {
        @Override
        public String getName() {
            return "shared/output.tsv";
        }
    };

    /**
     * ClassLoader object to view test resource files. Test files can be retrieved using {@code getResource()}, where an
     * empty {@link String} will refer to the folder {@code target/test-classes}.
     */
    protected final String mainDir = Thread.currentThread().getContextClassLoader().getResource("").getFile();

    /**
     * Returns the full path of the test resource.
     * @return {@link String}
     */
    public String getFullPathString() {
        return mainDir + getName();
    }

    /**
     * Returns the full path of the test resource.
     * @return {@link Path}
     */
    public Path getFullPath() {
        return Paths.get(getFullPathString());
    }

    /**
     * Returns the name of the test resource.
     * @return {@link String}
     */
    public abstract String getName();
}
