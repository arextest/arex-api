// package com.arextest.report.model.mapper;
//
// import com.arextest.report.model.api.contracts.configservice.CompareConfig;
// import com.arextest.report.model.api.contracts.configservice.ComparisonConfiguration;
// import com.arextest.report.model.api.contracts.configservice.ComparisonDetails;
// import com.arextest.report.model.api.contracts.configservice.ComparisonDetailsConfiguration;
// import org.mapstruct.Mapper;
// import org.mapstruct.factory.Mappers;
//
// import java.util.*;
// import java.util.stream.Collectors;
//
//
// @Mapper
// public interface ComparisonMapper {
//
//     ComparisonMapper INSTANCE = Mappers.getMapper(ComparisonMapper.class);
//
//     // CompareConfig fromConfig(List<ComparisonConfiguration> comparisonConfigurations);
//     // List<ComparisonConfiguration> toConfig(CompareConfig compareConfig);
//
//     default CompareConfig fromConfig(List<ComparisonConfiguration> comparisonConfigurations) {
//         CompareConfig compareConfig = new CompareConfig();
//         if (comparisonConfigurations == null || comparisonConfigurations.isEmpty()) {
//             return compareConfig;
//         }
//         Map<Integer, List<ComparisonConfiguration>> groupResult = comparisonConfigurations.stream()
//                 .collect(Collectors.groupingBy(ComparisonConfiguration::getCategoryType));
//         compareConfig.setExclusions(getExclusionOrInclusion(groupResult, 0));
//         compareConfig.setInclusions(getExclusionOrInclusion(groupResult, 7));
//         compareConfig.setSortKeys(getSortKeyOrReference(groupResult, 4));
//         compareConfig.setReferences(getSortKeyOrReference(groupResult, 5));
//         return compareConfig;
//     }
//
//     default List<ComparisonConfiguration> toConfig(CompareConfig compareConfig) {
//         List<ComparisonConfiguration> comparisonConfigurations = new ArrayList<>();
//         if (compareConfig == null){
//             return comparisonConfigurations;
//         }
//         comparisonConfigurations.add(fromExclusionOrInclusion(compareConfig.getExclusions(), 0));
//         comparisonConfigurations.add(fromExclusionOrInclusion(compareConfig.getInclusions(), 7));
//         comparisonConfigurations.add(fromSortKeyOrReference(compareConfig.getSortKeys(), 4));
//         comparisonConfigurations.add(fromSortKeyOrReference(compareConfig.getReferences(), 5));
//         return comparisonConfigurations.stream().filter(Objects::nonNull).collect(Collectors.toList());
//     }
//
//     default List<String> getExclusionOrInclusion(Map<Integer, List<ComparisonConfiguration>> groupResult, int categoryType) {
//
//         List<String> result = new ArrayList<>();
//         List<ComparisonConfiguration> comparisonConfigurations = groupResult.get(categoryType);
//         if (comparisonConfigurations == null || comparisonConfigurations.isEmpty() || comparisonConfigurations.get(0) == null) {
//             return null;
//         }
//         List<ComparisonDetailsConfiguration> detailsList = comparisonConfigurations.get(0).getDetailsList();
//         detailsList.stream()
//                 .filter(item -> item.getPathValue() != null && !item.getPathValue().isEmpty() && item.getPathValue().get(0) != null)
//                 .forEach(item -> {
//                     result.add(item.getPathValue().get(0));
//                 });
//         return result;
//     }
//
//     default List<ComparisonDetails> getSortKeyOrReference(Map<Integer, List<ComparisonConfiguration>> groupResult, int categoryType) {
//         List<ComparisonConfiguration> comparisonConfigurations = groupResult.get(categoryType);
//         if (comparisonConfigurations == null || comparisonConfigurations.isEmpty() || comparisonConfigurations.get(0) == null) {
//             return null;
//         }
//         List<ComparisonDetailsConfiguration> detailsConfigList = comparisonConfigurations.get(0).getDetailsList();
//         return detailsConfigList.stream().map(ComparisonDetailsMapper.INSTANCE::detailsFormConfig).collect(Collectors.toList());
//     }
//
//     default ComparisonConfiguration fromExclusionOrInclusion(Collection<String> list, int categoryType) {
//         if (list == null || list.isEmpty()) {
//             return null;
//         }
//         ComparisonConfiguration comparisonConfiguration = new ComparisonConfiguration();
//         List<ComparisonDetailsConfiguration> detailsConfigurations = new ArrayList<>();
//         list.stream().forEach(e -> {
//             ComparisonDetailsConfiguration detailsConfiguration = new ComparisonDetailsConfiguration();
//             detailsConfiguration.setPathName("");
//             detailsConfiguration.setPathValue(Arrays.asList(e));
//             detailsConfigurations.add(detailsConfiguration);
//         });
//         comparisonConfiguration.setCategoryType(categoryType);
//         comparisonConfiguration.setDetailsList(detailsConfigurations);
//         return comparisonConfiguration;
//     }
//
//     default ComparisonConfiguration fromSortKeyOrReference(Collection<ComparisonDetails> comparisonDetails, int categoryType) {
//         if (comparisonDetails == null || comparisonDetails.isEmpty()) {
//             return null;
//         }
//         ComparisonConfiguration comparisonConfiguration = new ComparisonConfiguration();
//         List<ComparisonDetailsConfiguration> detailsConfigurations = comparisonDetails.stream()
//                 .map(ComparisonDetailsMapper.INSTANCE::configFromDetails).collect(Collectors.toList());
//         comparisonConfiguration.setCategoryType(categoryType);
//         comparisonConfiguration.setDetailsList(detailsConfigurations);
//         return comparisonConfiguration;
//     }
//
// }
