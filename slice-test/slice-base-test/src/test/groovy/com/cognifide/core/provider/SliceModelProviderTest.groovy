package com.cognifide.core.provider

import com.cognifide.slice.test.module.SimpleModel
import com.cognifide.slice.test.setup.BaseSetup
import com.google.inject.Key
import com.google.inject.ProvisionException
import org.junit.Assert

/**
 * User: krzysztof.ryk@solsoft.pl
 * Date: 4/21/15 1:19 PM
 */
class SliceModelProviderTest extends BaseSetup {

    def setupSpec() {
        setup: "Create simple, initial content - node 'foo' with property 'prop1' set to 'prop1Value'"
        pageBuilder.content {
            foo("foo") {
                "jcr:content"("prop1": "prop1Value")
            }
        }
    }

    def "Content exists"() {
        expect:"Created content exists"
        assertPageExists("/content/foo")
    }

    def "Get model by class and path - non existing content"() {
        def nonExistingPath = "/content/bar"

        setup: "Creating model for non-existing path: " << nonExistingPath
        SimpleModel model = modelProvider.get(SimpleModel.class, nonExistingPath);

        expect: "Model should not be null"
        Assert.assertNotNull(model)

        and: "Model should have a null value for property 'prop1' set"
        Assert.assertNull(model.getProp1())
    }

    def "Get model by key and path - non existing content"() {
        def nonExistingPath = "/content/bar"

        setup: "Creating model for non-existing path: " << nonExistingPath
        SimpleModel model = modelProvider.get(Key.get(SimpleModel.class), nonExistingPath);

        expect: "Model should not be null"
        Assert.assertNotNull(model)

        and: "Model should have a null value for property 'prop1' set"
        Assert.assertNull(model.getProp1())
    }

    def "Get a list of models by class and path iterator - non existing content"() {
        def nonExistingPath1 = "/content/bar"
        def nonExistingPath2 = "/content/bar/foo"
        def nonExistingPaths = [nonExistingPath1, nonExistingPath2]

        setup: "Creating model for paths: " << nonExistingPaths
        def models = modelProvider.getList(SimpleModel.class, nonExistingPaths.iterator())

        expect: "List of models should not be null"
        Assert.assertNotNull(models)

        and: "List should have exactly 2 elements"
        Assert.assertEquals(2, models.size())

        and: "First Model should have a null value for property 'prop1' set"
        Assert.assertNull(models[0].getProp1())

        and: "Second Model should have a null value for property 'prop1' set"
        Assert.assertNull(models[1].getProp1())
    }

    def "Get a list of models by class and paths - non existing content"() {
        def nonExistingPath1 = "/content/bar"
        def nonExistingPath2 = "/content/bar/foo"
        def nonExistingPaths = [nonExistingPath1, nonExistingPath2]

        setup: "Creating model for paths: " << nonExistingPaths
        def models = modelProvider.getList(SimpleModel.class, nonExistingPaths.toArray(new String[0]))

        expect: "List of models should not be null"
        Assert.assertNotNull(models)

        and: "List should have exactly 2 elements"
        Assert.assertEquals(2, models.size())

        and: "First Model should have a null value for property 'prop1' set"
        Assert.assertNull(models[0].getProp1())

        and: "Second Model should have a null value for property 'prop1' set"
        Assert.assertNull(models[1].getProp1())
    }

    def "Get a list of models by class and resources iterator - non existing (null) resources"() {
        def nonExistingPath1 = "/content/bar"
        def nonExistingPath2 = "/content/bar/foo"
        def nonExistingResources = [resourceResolver.getResource(nonExistingPath1), resourceResolver.getResource(nonExistingPath2)]

        when: "Creating model for paths: " << nonExistingPath1 << nonExistingPath2
        modelProvider.getListFromResources(SimpleModel.class, nonExistingResources.iterator())

        then: "Throw ProvisionException - either path or resource should be set"
        thrown(ProvisionException)
    }

    def "Get Child Models of non-existing parent path"() {
        def nonExistingParentPath = "/content/bar"

        setup: "Creating child models for path: " + nonExistingParentPath
        def models = modelProvider.getChildModels(SimpleModel.class, nonExistingParentPath);

        expect: "Child models should not be null"
        Assert.assertNotNull(models)

        and: "Child models should be empty"
        Assert.assertEquals(0, models.size())
    }

    def "Get Child Models of non-existing parent resource"() {
        def nonExistingParentPath = "/content/bar"

        setup: "Creating child models for path: " + nonExistingParentPath
        def models = modelProvider.getChildModels(SimpleModel.class, resourceResolver.getResource(nonExistingParentPath));

        expect: "Child models should not be null"
        Assert.assertNotNull(models)

        and: "Child models should be empty"
        Assert.assertEquals(0, models.size())
    }

    def "Get Child Models of existing parentPath with no children"() {
        def parentPathWithNoChildren = "/content/bar"

        setup: "Creating parent node with no children, under '/content/bar'"
        pageBuilder.content {
            bar("bar")
        }
        and: "Creating child models for path: " + parentPathWithNoChildren
        def models = modelProvider.getChildModels(SimpleModel.class, parentPathWithNoChildren)

        expect: "Child models should not be null"
        Assert.assertNotNull(models)

        and: "Child models should not be empty"
        Assert.assertNotSame(0, models.size())

        and: "Elements in models list should not have any property set"
        models.each {
            model -> Assert.assertNull(model.getProp1())
        }
    }

    def "Get model by a null path"() {
        def nullPath = null

        when: "Creating model based on null path"
        modelProvider.get(SimpleModel.class, nullPath)

        then: "Throw ProvisionException - either path or resource should be set"
        thrown(ProvisionException)
    }

    def "Get model by a resource - non-existing (null) resource"() {
        def nonExistingPath = "/content/bar"

        setup: "Get non-existing resource"
        def resource = resourceResolver.getResource(nonExistingPath)

        when: "Creating model for non-existing (null) resource"
        modelProvider.get(SimpleModel.class, resource);

        then: "Throw ProvisionException - either path or resource should be set"
        thrown(ProvisionException)
    }

    def "Get model resource by key and resource - non-existing (null) resource"() {
        def nonExistingPath = "/content/bar"

        setup: "Get non-existing resource"
        def resource = resourceResolver.getResource(nonExistingPath)

        when: "Creating model for non-existing (null) resource"
        modelProvider.get(Key.get(SimpleModel.class), resource);

        then: "Throw ProvisionException - either path or resource should be set"
        thrown(ProvisionException)
    }

    //    /**
//     * Unable to test this method, due to classToKeyMapper usage - it would require some mocks. Needs to be investigated.
//     */
//    def "Create model based on non-existing content by class name and path"() {
//        def nonExistingPath = "/content/bar";
//        setup: "Creating model for path: " + nonExistingPath
//        SimpleModel model = modelProvider.get(SimpleModel.class.getName(), nonExistingPath);
//
//        expect: "Model should not be null"
//        Assert.assertNotNull(model)
//
//        and: "Model should have a null value for property 'prop1' set"
//        Assert.assertNull(model.getProp1())
//    }

//    /**
//     * Unable to test this method, due to classToKeyMapper usage - it would require some mocks. Needs to be investigated.
//     */
//    def "Create model based on non-existing content by class name and resource"() {
//        def nonExistingPath = "/content/bar";
//        setup: "Get non-existing resource"
//        def resource = resourceResolver.getResource(nonExistingPath)
//        and: "Creating model for non-existing resource"
//        SimpleModel model = modelProvider.get(SimpleModel.getClass().getName(), resource);
//
//        expect: "Resource should be null"
//        Assert.assertNull(resource)
//        and: "Model should not be null"
//        Assert.assertNotNull(model)
//        and: "Model should have a null value for property 'prop1' set"
//        Assert.assertNull(model.getProp1())
//    }

    def cleanup() {
        removeAllNodes()
    }

}
