package com.dawidweiss.util.lang;

import java.util.Vector;



/**
 * This class loader permits to count all class references that are loaded
 * by instances of classes loaded by it. It also supports dynamic class repositories
 * (filesystem directories and jar files), via AdaptiveClassLoader.
 */
public class AccountingClassLoader
        extends AdaptiveClassLoader
{
    private long classesLoaded;
    private long classesReferenced;
    private long classesFromParentLoader;
    private long classesNotFound;

    private boolean debug = false;


    /**
     * Sets the debug mode in which loaded classes are dumped to stdout.
     */
    public void setDebug( boolean debug )
    {
        this.debug = debug;
    }


    /**
     * Creates a new class loader that will load classes from specified
     * class repositories.
     *
     * @param classRepository An set of File classes indicating
     *        directories and/or zip/jar files. It may be empty when
     *        only system classes are loaded.
     * @throw java.lang.IllegalArgumentException if the objects contained
     *        in the vector are not a file instance or the file is not
     *        a valid directory or a zip/jar file.
     */
    public AccountingClassLoader(Vector classRepository, boolean systemClassesFirst)
        throws IllegalArgumentException
    {
        super(classRepository, systemClassesFirst);
    }


    /**
     * Creates a new class loader that will load classes from specified
     * class repositories.
     *
     * @param classRepository An set of File classes indicating
     *        directories and/or zip/jar files. It may be empty when
     *        only system classes are loaded.
     * @param chainedClassLoader A class loader to attempt to load classes
     *        as resources thru before falling back on the default system
     *        loaders.
     * @throw java.lang.IllegalArgumentException if the objects contained
     *        in the vector are not a file instance or the file is not
     *        a valid directory or a zip/jar file.
     */
    public AccountingClassLoader(Vector classRepository, ClassLoader chainedClassLoader, boolean systemClassesFirst)
        throws IllegalArgumentException
    {
        super( classRepository, chainedClassLoader, systemClassesFirst );
    }


    /**
     * Returns the count of loaded classes (successfully resolved).
     */
    public long getClassesLoadedCount()
    {
        return classesLoaded;
    }


    /**
     * Returns the count of classes resolved using parent or system class loader.
     */
    public long getClassesFromParentLoaderCount()
    {
        return classesFromParentLoader;
    }


    /**
     * Returns the count of referenced classes (successfuly resolved or not).
     */
    public long getClassesReferencedCount()
    {
        return classesReferenced;
    }


    /**
     * Returns the count of unsuccessfuly class lookups.
     */
    public long getClassesNotFoundCount()
    {
        return classesNotFound;
    }


    /**
     * Resets all counters.
     */
    public synchronized void resetCounters()
    {
        this.classesLoaded = 0;
        this.classesFromParentLoader = 0;
        this.classesReferenced = 0;
        this.classesNotFound = 0;
    }



    public Class loadClass(String name)
            throws java.lang.ClassNotFoundException
    {
        return this.loadClass( name, false );
    }


    protected synchronized Class loadClass(String name, boolean resolve)
            throws java.lang.ClassNotFoundException
    {
        classesReferenced++;

        if (debug) System.err.println( "Load class: " + name );
        try
        {
            Class reference = super.loadClass(name, resolve);
            if (resolve == false)
                classesLoaded++;
            if (debug && reference==null) System.err.println( "## FAILED load class: " + name);
            return reference;
        }
        catch (ClassNotFoundException e)
        {
            classesNotFound++;
            throw e;
        }
    }


    protected Class loadSystemClass(String name, boolean resolve)
        throws NoClassDefFoundError, ClassNotFoundException
    {
        if (debug) System.err.println("Loading class from system loader: " + name );
        classesFromParentLoader++;
        Class p = super.loadSystemClass(name, resolve);
        if (debug && p==null) System.err.println( "## FAILED load class: " + name);
        return p;
    }


    public String toString()
    {
        return "Accounting Class Loader ["
            + "Referenced=" + this.getClassesReferencedCount() + " "
            + "Loaded=" + this.getClassesLoadedCount() + " "
            + "From parent=" + this.getClassesFromParentLoaderCount() + " "
            + "Not found=" + this.getClassesNotFoundCount() + "]";
    }
}