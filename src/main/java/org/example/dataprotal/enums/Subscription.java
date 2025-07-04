package org.example.dataprotal.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum Subscription {
    FREE(BigDecimal.valueOf(0), BigDecimal.valueOf(0),
            Map.of(Language.EN,
                    List.of("Access to open data from the main portal",
                            "View public dashboards",
                            "Read open data",
                            "Limited search functions"),
                    Language.AZE,
                    List.of("Əsas portaldan açıq məlumatlara giriş",
                            "İctimai dashboardlara baxış",
                            "Açıq məlumatların oxunması",
                            "Məhdud axtarış funksiyalar"),
                    Language.RU,
                    List.of("Доступ к открытым данным с главного портала",
                            "Просмотр общедоступных панелей управления",
                            "Чтение открытых данных",
                            "Ограниченные функции поиска"))),
    STANDARD(BigDecimal.valueOf(10), BigDecimal.valueOf(96),
            Map.of(Language.EN,
                    List.of("Download additional analytical capabilities and reports",
                            "Download documents in PDF and Excel format,",
                            "Advanced search and filter functions",
                            "Receive email notifications"),
                    Language.AZE,
                    List.of("Əlavə analitik imkanlar və hesabat yükləmək",
                            "PDF və Excel formatında sənədləri yükləmək",
                            "Daha geniş axtarış və filtr funksiyaları",
                            "E-poçta yenilik bildirişləri almaq"),
                    Language.RU,
                    List.of("Загрузка дополнительных аналитических возможностей и отчетов",
                            "Загрузка документов в формате PDF и Excel",
                            "Расширенные функции поиска и фильтрации",
                            "Получать уведомления по электронной почте"))),
    PREMIUM(BigDecimal.valueOf(0), BigDecimal.valueOf(0),
            Map.of(Language.EN,
                    List.of("Additional analytical capabilities and report downloads",
                            "Download documents in PDF and Excel format",
                            "Advanced search and filter functions",
                            "Receive email notifications",
                            "Data upload and sharing",
                            "Access to exclusive dashboards",
                            "Fast support (priority support)"),
                    Language.AZE,
                    List.of("Əlavə analitik imkanlar və hesabat yükləmək",
                            "PDF və Excel formatında sənədləri yükləmək",
                            "Daha geniş axtarış və filtr funksiyaları",
                            "E-poçta yenilik bildirişləri almaq",
                            "Data yükləmə və paylaşma",
                            "Eksklüziv dashboardlara çıxış",
                            "Sürətli dəstək (priority support)"),
                    Language.RU,
                    List.of("Дополнительные аналитические возможности и загрузка отчетов",
                            "Загрузка документов в формате PDF и Excel",
                            "Расширенные функции поиска и фильтрации",
                            "Получите уведомления по электронной почте",
                            "Загрузка и обмен данными",
                            "Доступ к эксклюзивным панелям мониторинга",
                            "Быстрая поддержка (приоритетная поддержка)")));

    BigDecimal priceForOneMonth;

    BigDecimal priceForOneYear;

    Map<Language, List<String>> advantages;
}