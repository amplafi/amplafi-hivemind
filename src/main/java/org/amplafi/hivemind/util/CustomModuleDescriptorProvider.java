/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amplafi.hivemind.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.ErrorHandler;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.ModuleDescriptorProvider;
import org.apache.hivemind.Resource;
import org.apache.hivemind.parse.ModuleDescriptor;
import org.apache.hivemind.parse.SubModuleDescriptor;
import org.apache.hivemind.parse.XmlResourceProcessor;
import org.apache.hivemind.util.URLResource;

/**
 * Implementation of the {@link ModuleDescriptorProvider} interface which uses the
 * {@link org.apache.hivemind.parse.DescriptorParser} to provide module descriptors defined in XML.
 * The module descriptors are loaded from files or resources on the classpath.
 * 
 * Descriptors found on the file system are NOT included.
 *
 * @author Knut Wannheden
 * @author Andreas Androeu
 */
public class CustomModuleDescriptorProvider implements ModuleDescriptorProvider
{
    private static final Log LOG = LogFactory.getLog(CustomModuleDescriptorProvider.class);

    /**
     * The default path, within a JAR or the classpath, to the XML HiveMind module deployment
     * descriptor: <code>META-INF/hivemodule.xml</code>. Use this constant with the
     * {@link #XmlModuleDescriptorProvider(ClassResolver, String)} constructor.
     */
    public static final String HIVE_MODULE_XML = "META-INF/hivemodule.xml";

    /**
     * Set of all specified resources processed by this ModuleDescriptorProvider. Descriptors of
     * sub-modules are not included.
     */
    private List _resources = new ArrayList();

    /**
     * List of parsed {@link ModuleDescriptor} instances. Also includes referenced sub-modules.
     */
    private List _moduleDescriptors = new ArrayList();

    private ClassResolver _resolver;

    private ErrorHandler _errorHandler;

    /**
     * Parser instance used by all parsing of module descriptors.
     */
    private XmlResourceProcessor _processor;

    private boolean excludeFiles;

    private boolean excludeJars;

    private String excludePattern;

    /**
     * Convenience constructor. Equivalent to using
     * {@link #XmlModuleDescriptorProvider(ClassResolver, String)}with {@link #HIVE_MODULE_XML} as
     * the second argument.
     */
    public CustomModuleDescriptorProvider(ClassResolver resolver)
    {
        this(resolver, HIVE_MODULE_XML);
    }

    /**
     * Loads all XML module descriptors found on the classpath (using the given
     * {@link org.apache.hivemind.ClassResolver}. Only module descriptors matching the specified
     * path are loaded. Use the {@link XmlModuleDescriptorProvider#HIVE_MODULE_XML} constant to load
     * all descriptors in the default location.
     */
    public CustomModuleDescriptorProvider(ClassResolver resolver, String resourcePath)
    {
        _resolver = resolver;
        _resources.addAll(getDescriptorResources(resourcePath, _resolver));
    }

    /**
     * Constructs an XmlModuleDescriptorProvider only loading the ModuleDescriptor identified by the
     * given {@link org.apache.hivemind.Resource}.
     */
    public CustomModuleDescriptorProvider(ClassResolver resolver, Resource resource)
    {
        _resolver = resolver;
        _resources.add(resource);
    }

    /**
     * Constructs an XmlModuleDescriptorProvider loading all ModuleDescriptor identified by the
     * given List of {@link org.apache.hivemind.Resource} objects.
     */
    public CustomModuleDescriptorProvider(ClassResolver resolver, List resources)
    {
        _resolver = resolver;
        _resources.addAll(resources);
    }

    private List getDescriptorResources(String resourcePath, ClassResolver resolver)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Processing modules visible to " + resolver);

        List descriptors = new ArrayList();

        ClassLoader loader = resolver.getClassLoader();
        Enumeration e = null;

        try
        {
            e = loader.getResources(resourcePath);
        }
        catch (IOException ex)
        {
            throw new ApplicationRuntimeException("UnableToFindModules(" + resolver + ", " + ex + ")",
                    ex);
        }

        Pattern pattern = excludePattern==null ?
            null : Pattern.compile(excludePattern);

        while (e.hasMoreElements())
        {
            URL descriptorURL = (URL) e.nextElement();
            LOG.debug(descriptorURL);

            String protocol = descriptorURL.getProtocol();
            
            if (excludeFiles && protocol.equals("file")) {
                continue;
            }
            
            if (excludeJars && protocol.equals("jar")) {
                continue;
            }
            
            if (pattern!=null && pattern.matcher(descriptorURL.toString()).matches()) {
                continue;
            }

            descriptors.add(new URLResource(descriptorURL));
        }

        return descriptors;
    }

    public List getModuleDescriptors(ErrorHandler handler)
    {
        _errorHandler = handler;

        _processor = getResourceProcessor(_resolver, handler);

        for (Iterator i = _resources.iterator(); i.hasNext();)
        {
            Resource resource = (Resource) i.next();

            processResource(resource);
        }

        _processor = null;

        _errorHandler = null;

        return _moduleDescriptors;
    }

    private void processResource(Resource resource)
    {
        try
        {
            ModuleDescriptor md = _processor.processResource(resource);

            _moduleDescriptors.add(md);

            // After parsing a module, parse any additional modules identified
            // within the module (using the <sub-module> element) recursively.
            processSubModules(md);
        }
        catch (RuntimeException ex)
        {
            _errorHandler.error(LOG, ex.getMessage(), HiveMind.getLocation(ex), ex);
        }
    }

    private void processSubModules(ModuleDescriptor moduleDescriptor)
    {
        List subModules = moduleDescriptor.getSubModules();

        if (subModules == null)
            return;

        for (Iterator i = subModules.iterator(); i.hasNext();)
        {
            SubModuleDescriptor smd = (SubModuleDescriptor) i.next();

            Resource descriptorResource = smd.getDescriptor();

            if (descriptorResource.getResourceURL() == null)
            {
                _errorHandler.error(
                        LOG,
                        "subModuleDoesNotExist:" + descriptorResource,
                        smd.getLocation(),
                        null);
                continue;
            }

            processResource(smd.getDescriptor());
        }
    }

    protected XmlResourceProcessor getResourceProcessor(ClassResolver resolver, ErrorHandler handler)
    {
        return new XmlResourceProcessor(resolver, handler);
    }

    public void setExcludeFiles(boolean excludeFiles) {
        this.excludeFiles = excludeFiles;
    }

    public void setExcludeJars(boolean excludeJars) {
        this.excludeJars = excludeJars;
    }

    public void setExcludePattern(String excludePattern) {
        this.excludePattern = excludePattern;
    }
}
