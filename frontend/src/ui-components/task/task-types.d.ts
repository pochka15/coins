interface TNewTask {
  title: string
  content: string
  deadline?: Date
  budget: number
}

declare interface TMemberInformation {
  isAssignee: boolean
  isAuthor: boolean
}

declare interface TTaskPermissions {
  canSolve: boolean
  canAccept: boolean
  canReject: boolean
}
