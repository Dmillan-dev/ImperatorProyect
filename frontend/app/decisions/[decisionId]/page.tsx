import { DecisionReviewWorkspace } from "@/features/decision-review/decision-review-workspace";

export default async function DecisionPage({
  params,
}: {
  params: Promise<{ decisionId: string }>;
}) {
  const { decisionId } = await params;
  return <DecisionReviewWorkspace decisionId={decisionId} />;
}
